# JuntaVecinos

Aplicación Android creada para llevar el control de reservas de espacios comunitarios y los movimientos de dinero de una junta de vecinos.

**Integrantes:** Gabriel Bustamante y Cristian Flores

## Funcionalidades

- **Usuarios:** registro, inicio y cierre de sesión. Las contraseñas se almacenan como hashes SHA-256 con una sal aleatoria.
- **Reservas:** registrar, consultar y eliminar reservas. El formulario valida los datos y evita horarios superpuestos en la misma fecha.
- **Finanzas:** registrar ingresos y gastos, consultar movimientos y ver el saldo disponible.
- **Cámara:** tomar y visualizar una foto de un comprobante al registrar un movimiento.
- **Ubicación:** consultar las coordenadas actuales del dispositivo, con permiso del usuario.

## Tecnologías

Kotlin, Jetpack Compose, Material 3, MVVM, StateFlow y Room para el almacenamiento local.

## Cómo ejecutar

1. Descargar o clonar el repositorio.
2. Abrir el proyecto en Android Studio y esperar la sincronización de Gradle.
3. Iniciar un emulador o conectar un celular Android.
4. Ejecutar la aplicación desde el módulo `app`.

Los usuarios, las reservas y los movimientos se guardan localmente. Las fotos de los comprobantes se muestran como vista previa, pero no se guardan de forma permanente.
