import http from 'k6/http';
import { check, fail, sleep } from 'k6';
import { Counter } from 'k6/metrics';

const base = __ENV.BASE_URL || 'http://portal';

const solicitudes = new Counter('solicitudes_rnf03');

const endpoints = [
  'cursos_docente',
  'inscripciones_estudiante',
  'historial_asistencias',
  'perfil_estudiante',
];

const umbrales = {};

for (const endpoint of endpoints) {
  umbrales[`http_req_duration{endpoint:${endpoint}}`] = [
    'p(95)<2000',
  ];

  umbrales[`http_req_failed{endpoint:${endpoint}}`] = [
    'rate==0',
  ];

  umbrales[`checks{endpoint:${endpoint}}`] = [
    'rate==1',
  ];

  umbrales[`solicitudes_rnf03{endpoint:${endpoint}}`] = [
    'count>=100',
  ];
}

export const options = {
  scenarios: {
    prueba_rnf03: {
      executor: 'per-vu-iterations',
      vus: 20,
      iterations: 5,
      maxDuration: '5m',
    },
  },

  thresholds: umbrales,
};

function iniciarSesion(email, password) {
  const respuesta = http.post(
    `${base}/api/v1/auth/login`,
    JSON.stringify({ email, password }),
    {
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
    },
  );

  if (respuesta.status !== 200) {
    fail(`No se pudo iniciar sesión: HTTP ${respuesta.status}`);
  }

  const token = respuesta.json('token');

  if (!token || typeof token !== 'string') {
    fail('La API no devolvió un token válido.');
  }

  return token;
}

export function setup() {
  const password = __ENV.DEMO_PASSWORD;

  if (!password) {
    fail('Falta configurar DEMO_PASSWORD.');
  }

  return {
    docente: iniciarSesion(
      'teacher.owner@example.test',
      password,
    ),

    estudiante: iniciarSesion(
      'student.enrolled@example.test',
      password,
    ),
  };
}

export default function (sesiones) {
  const casos = [
    {
      id: 'cursos_docente',
      ruta: '/api/v1/cursos',
      token: sesiones.docente,
    },
    {
      id: 'inscripciones_estudiante',
      ruta: '/api/v1/enrollments/me',
      token: sesiones.estudiante,
    },
    {
      id: 'historial_asistencias',
      ruta: '/api/v1/asistencias/historial',
      token: sesiones.estudiante,
    },
    {
      id: 'perfil_estudiante',
      ruta: '/api/v1/usuarios/me',
      token: sesiones.estudiante,
    },
  ];

  for (const caso of casos) {
    const respuesta = http.get(
      `${base}${caso.ruta}`,
      {
        headers: {
          Authorization: `Bearer ${caso.token}`,
          Accept: 'application/json',
        },
        tags: {
          endpoint: caso.id,
          name: caso.id,
        },
      },
    );

    solicitudes.add(1, {
      endpoint: caso.id,
    });

    check(
      respuesta,
      {
        'Respuesta HTTP 200': (r) => r.status === 200,
      },
      {
        endpoint: caso.id,
      },
    );
  }

  sleep(0.5);
}