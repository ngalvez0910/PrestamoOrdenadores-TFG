# LoanTech - Sistema de Préstamo de Ordenadores

## 📋 Descripción

LoanTech es un sistema de gestión integral para el préstamo de equipos informáticos. Permite a instituciones educativas gestionar de manera eficiente el inventario de ordenadores y controlar los préstamos a usuarios.

## ✨ Características

- **Gestión de Inventario**: Control completo del stock de ordenadores disponibles
- **Sistema de Préstamos**: Registro y seguimiento de préstamos activos
- **Gestión de Usuarios**: Administración de perfiles de prestatarios
- **Historial de Transacciones**: Registro detallado de todos los préstamos
- **Notificaciones**: Alertas automáticas para devoluciones pendientes
- **Reportes**: Generación de informes de uso y disponibilidad
- **Dashboard Administrativo**: Panel de control con métricas en tiempo real

## 🛠️ Tecnologías Utilizadas

- **Frontend**: Vue
- **Backend**: Kotlin, Springboot
- **Base de Datos**: PostgreSQL
- **Autenticación**: JWT
- **Estilos**: CSS3 / Tailwind CSS

## 📦 Instalación

### Prerrequisitos

- Docker
- Base de datos (PostgreSQL)

### 👣 Pasos de Instalación

1. Clona el repositorio:
```bash
git clone [https://github.com/tu-usuario/loantech.git](https://github.com/ngalvez0910/PrestamoOrdenadores-TFG)
cd PrestamoOrdenadores-TFG
```

2. Instala las dependencias:
```bash
./gradlew build
```

3. Inicia el servidor de producción:
```bash
docker compose -f docker-compose.prod.yml down --volumes --rmi all
docker compose -f docker-compose.prod.yml up -d --build
```

## 🚀 Uso

### Acceso al Sistema

1. Añade el dominio `loantechoficial.com` a tu fichero host
2. Accede a `https://loantechoficial.com`
3. Inicia sesión con las credenciales de administrador
4. Comienza a gestionar el inventario y préstamos

### Funcionalidades Principales

#### Gestión de Equipos
- Agregar nuevos ordenadores al inventario
- Actualizar especificaciones y estado
- Marcar equipos como disponibles/no disponibles

#### Gestión de Préstamos
- Crear nuevos préstamos
- Registrar devoluciones
- Generar comprobantes

#### Reportes
- Historial de mantenimiento
- Copia de seguridad

## 🧪 Testing

- MockK
- JUnit
- TestContainers
- Cypress

## 👥 Autores

- **Natalia González Álvarez** - [https://github.com/ngalvez0910](https://github.com/ngalvez0910)
