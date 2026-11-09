
import { useEffect, useRef, useState } from 'react'
import { Html5Qrcode } from 'html5-qrcode'

interface Props {
  onScan: (qr: string) => void
}

export default function StudentQrScanner({ onScan }: Props) {
  const scannerRef = useRef<Html5Qrcode | null>(null)
  const onScanRef = useRef(onScan)
  const [active, setActive] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    onScanRef.current = onScan
  }, [onScan])

  useEffect(() => {
    if (!active) return

    let cancelled = false
    let detected = false
    const scanner = new Html5Qrcode('student-qr-reader')
    scannerRef.current = scanner

    async function start() {
      try {
        await scanner.start(
          { facingMode: 'environment' },
          { fps: 10, qrbox: { width: 220, height: 220 } },
          (decodedText) => {
            if (detected || cancelled) return
            detected = true
            onScanRef.current(decodedText)
            setActive(false)
          },
          () => {},
        )

        if (cancelled && scanner.isScanning) {
          await scanner.stop()
        }
      } catch {
        if (!cancelled) {
          setError('No se pudo acceder a la cámara.')
          setActive(false)
        }
      }
    }

    void start()

    return () => {
      cancelled = true
      if (scanner.isScanning) {
        void scanner.stop().catch(() => {})
      }
      if (scannerRef.current === scanner) {
        scannerRef.current = null
      }
    }
  }, [active])

  return (
    <section>
      <h2>Escanear código QR</h2>
      <p>Escanea el código generado por tu docente.</p>

      <button
        type="button"
        onClick={() => {
          setError('')
          setActive((current) => !current)
        }}
      >
        {active ? 'Detener cámara' : 'Abrir cámara'}
      </button>

      {active && <div id="student-qr-reader" />}

      {error && <p role="alert">{error}</p>}
    </section>
  )
}
