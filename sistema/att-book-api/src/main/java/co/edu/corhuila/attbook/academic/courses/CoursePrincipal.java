package co.edu.corhuila.attbook.academic.courses;

import java.security.Principal;

public interface CoursePrincipal extends Principal {

    Long getUserId();

    String getRole();
}