# Sistema de Gestión de Trámites Ambientales (Cormacarena)

Este proyecto implementa una solución de automatización de procesos de negocio (BPM) utilizando **Camunda Platform 7** y **Spring Boot**. El sistema orquesta trámites ambientales como Licenciamiento, PQRDS y Procesos Sancionatorios, dividiendo la arquitectura en microservicios o módulos independientes para garantizar la separación de responsabilidades.

## 🏗 Arquitectura del Proyecto

El proyecto está organizado en un monorepo con cuatro módulos Maven principales:

### 1\. `engine-server` (Puerto: 8080)

  * **Responsabilidad:** Es el corazón del sistema. Contiene el motor de Camunda (BPMN Engine), la base de datos H2 y los recursos de procesos (`.bpmn`, `.dmn`, `.form`).
  * **Componentes Clave:**
      * Base de datos H2 embebida (persistencia en archivo).
      * API REST de Camunda habilitada.
      * Definiciones de procesos: *Licenciamiento Ambiental*, *PQRDS*, *Sancionatorio Ambiental*.

### 2\. `client-app` (Puerto: 8081)

  * **Responsabilidad:** Portal web para el ciudadano (Frontend/Backend).
  * **Funcionalidades:**
      * Radicación de solicitudes (Licencias, Denuncias, PQRDS).
      * Consulta de estado de trámites.
      * Bandeja de tareas del ciudadano (subsanaciones, pagos).

### 3\. `employee-app` (Puerto: 8082)

  * **Responsabilidad:** Intranet para funcionarios de la entidad.
  * **Funcionalidades:**
      * Bandeja de entrada filtrada por roles (Coordinador, Técnico, Jurídica, Director, etc.).
      * Gestión de tareas de usuario (aprobaciones, conceptos técnicos, firmas).
      * Historial de trámites.

### 4\. `transactional-worker` (Puerto: 9090)

  * **Responsabilidad:** Ejecución de tareas automáticas (External Tasks).
  * **Funcionalidades:**
      * Generación de consecutivos y referencias de pago.
      * Envío de notificaciones (simuladas en consola).
      * Validaciones de reglas de negocio y cálculos.

-----

## 🚀 Requisitos Previos

  * **Java:** JDK 21
  * **Maven:** 3.8+
  * **IDE:** IntelliJ IDEA, Eclipse o VS Code (con extensiones de Java).

-----

## 🛠️ Instrucciones de Ejecución

Para levantar el ecosistema completo, debes ejecutar cada módulo por separado. Se recomienda seguir este orden:

### 1\. Compilación General

Desde la raíz del proyecto:

```bash
mvn clean install
```

### 2\. Iniciar el Motor (Engine)

```bash
cd engine-server
mvn spring-boot:run
```

  * Esperar a que inicie en el puerto **8080**.
  * Acceso a Cockpit: [http://localhost:8080/camunda/app/cockpit/](https://www.google.com/search?q=http://localhost:8080/camunda/app/cockpit/)
  * **Credenciales:** `demo` / `demo`

### 3\. Iniciar el Worker (Tareas Automáticas)

```bash
cd transactional-worker
mvn spring-boot:run
```

  * Inicia en el puerto **9090**. Se conectará automáticamente al Engine para suscribirse a los tópicos.

### 4\. Iniciar Aplicaciones Web

En terminales separadas:

**Portal Ciudadano:**

```bash
cd client-app
mvn spring-boot:run
```

  * Acceso: [http://localhost:8081/](https://www.google.com/search?q=http://localhost:8081/)

**Portal Funcionarios:**

```bash
cd employee-app
mvn spring-boot:run
```

  * Acceso: [http://localhost:8082/](https://www.google.com/search?q=http://localhost:8082/)

-----

## 🔄 Procesos Implementados

### 1\. Licenciamiento Ambiental (`Licenciamiento Ambiental.bpmn`)

Flujo para otorgar licencias a proyectos de impacto.

  * **Actores:** Solicitante, Coordinador, Técnico, Jurídica, Director.
  * **Hitos:** Validación documental, Pago de evaluación, Visita técnica, Concepto técnico, Pago de licencia, Resolución final.

### 2\. PQRDS (`PQRDS.bpmn`)

Gestión de Peticiones, Quejas, Reclamos, Denuncias y Sugerencias.

  * **Actores:** Ventanilla, Gestor Documental, Jefe PQRDS, Profesional asignado.
  * **Flujo:** Recepción, verificación de competencia, asignación, respuesta técnica, firma y notificación.

### 3\. Sancionatorio Ambiental (`SancionatorioAmbiental.bpmn`)

Proceso punitivo ante infracciones ambientales.

  * **Subprocesos:** Incluye subprocesos para actos administrativos (`ElaborarActoAdministrativo.bpmn`).
  * **Hitos:** Radicación de denuncia, Flagrancia, Formulación de cargos, Descargos, Recursos de reposición, Sanción (Multa/Demolición/Compensación) o Exoneración.
  * **Reglas:** Usa DMN (`determinarSancion.dmn`) para calcular multas base.

-----

## 👤 Roles de Usuario (Employee App)

Para probar la aplicación de funcionarios (`localhost:8082`), puedes filtrar la bandeja usando los siguientes roles en la URL o el menú de navegación:

  * **Coordinador:** Asignación de profesionales y validación inicial.
  * **Técnico:** Visitas de campo y conceptos técnicos.
  * **Juridica:** Elaboración de resoluciones y actos administrativos.
  * **Director:** Firma final de licencias.
  * **Gestor / JefePQRDS:** Gestión de PQRDS.
  * **Portal:** Ventanilla de recepción.

-----

## ⚙️ Configuración Técnica

  * **Base de Datos:** H2 (Archivo local). La persistencia se guarda en `./camunda-h2-database`.
  * **Comunicación:** Las aplicaciones web (`client-app`, `employee-app`) y el `worker` se comunican con `engine-server` a través de la **REST API** de Camunda.
  * **Formularios:** Se utilizan formularios embebidos de Camunda y formularios HTML renderizados con Thymeleaf en las aplicaciones cliente.
