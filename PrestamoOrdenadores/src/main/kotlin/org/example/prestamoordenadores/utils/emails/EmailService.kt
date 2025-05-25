package org.example.prestamoordenadores.utils.emails

import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class  EmailService(private val mailSender: JavaMailSender) {

    @Value("\${spring.mail.username}")
    private lateinit var senderEmail: String

    fun sendHtmlEmailPrestamoCreado(to: String, subject: String, nombreUsuario: String, numeroSerieDispositivo: String, fechaDevolucion: String, pdfBytes: ByteArray, nombreArchivoPdf: String) {
        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(
            mimeMessage,
            true,
            "UTF-8"
        )

        helper.setFrom(senderEmail)
        helper.setTo(to)
        helper.setSubject(subject)

        val htmlContent = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>$subject</title>
                <style>
                    body { font-family: sans-serif; }
                    .header { background-color: #14124f; color: #fff; padding: 20px; text-align: center; }
                    .content { padding: 20px; }
                    .footer { margin-top: 20px; font-size: 0.8em; color: #14124f; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>LoanTech</h1>
                </div>
                <div class="content">
                    <h3>Confirmación de Préstamo</h3>
                    <p>Estimado/a <strong>${nombreUsuario}</strong>,</p>
                    <p>Usted ha realizado un nuevo préstamo.</p>
                    <h3>Detalles del préstamo:</h3>
                    <ul>
                        <li><strong>Dispositivo:</strong> ${numeroSerieDispositivo}</li>
                        <li><strong>Fecha de Devolución:</strong> ${fechaDevolucion}</li>
                    </ul>
                    <p>A continuación está adjuntado el PDF con los detalles del préstamo. <br>
                    Por favor, preséntelo en el departamento de informática para recoger su dispositivo.
                    </p>
                </div>
                <div class="footer">
                    Este es un correo electrónico automático. Por favor, no respondas a este mensaje.
                </div>
            </body>
            </html>
        """.trimIndent()

        helper.setText(htmlContent, true)

        helper.addAttachment(nombreArchivoPdf, ByteArrayResource(pdfBytes))

        try {
            mailSender.send(mimeMessage)
            println("Correo electrónico HTML con adjunto enviado a: $to")
        } catch (e: Exception) {
            println("Error al enviar el correo electrónico HTML con adjunto a $to: ${e.message}")
        }
    }

    fun sendHtmlEmailSancion(to: String, subject: String, nombreUsuario: String, tipoSancion: String) {
        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(
            mimeMessage,
            true,
            "UTF-8"
        )

        helper.setFrom(senderEmail)
        helper.setTo(to)
        helper.setSubject(subject)

        val htmlContent = """
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>$subject</title>
            <style>
                body { font-family: sans-serif; }
                .header { background-color: #14124f; color: #fff; padding: 20px; text-align: center; }
                .content { padding: 20px; }
                .footer { margin-top: 20px; font-size: 0.8em; color: #14124f; }
            </style>
        </head>
        <body>
            <div class="header">
                <h1>LoanTech</h1>
            </div>
            <div class="content">
                <h3>Notificación de Sanción</h3>
                <p>Estimado/a <strong>${nombreUsuario}</strong>,</p>
                <p>Le informamos que se le ha aplicado una nueva sanción.</p>
                <h3>Detalles de la sanción:</h3>
                <ul>
                    <li><strong>Tipo de Sanción:</strong> ${tipoSancion}</li>
                </ul>
                <p>Le recomendamos revisar sus préstamos y devolver aquellos que no haya entregado aún. Si tiene alguna pregunta o necesita aclaración, por favor, póngase en contacto con el departamento de informática.</p>
            </div>
            <div class="footer">
                Este es un correo electrónico automático. Por favor, no responda a este mensaje.
            </div>
        </body>
        </html>
    """.trimIndent()

        helper.setText(htmlContent, true)

        try {
            mailSender.send(mimeMessage)
            println("Correo electrónico HTML de sanción enviado a: $to")
        } catch (e: Exception) {
            println("Error al enviar el correo electrónico HTML de sanción a $to: ${e.message}")
        }
    }

    fun sendHtmlEmailPrestamoApuntodeCaducar(to: String, subject: String, nombreUsuario: String, numeroSerieDispositivo: String, fechaDevolucion: String) {
        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

        helper.setFrom(senderEmail)
        helper.setTo(to)
        helper.setSubject(subject)

        val htmlContent = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>$subject</title>
                <style>
                    body { font-family: sans-serif; }
                    .header { background-color: #FFA500; color: #fff; padding: 20px; text-align: center; } /* Naranja para advertencia */
                    .content { padding: 20px; }
                    .footer { margin-top: 20px; font-size: 0.8em; color: #14124f; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>LoanTech - Recordatorio Importante</h1>
                </div>
                <div class="content">
                    <h3>¡Tu Préstamo Está a Punto de Caducar!</h3>
                    <p>Estimado/a <strong>${nombreUsuario}</strong>,</p>
                    <p>Queremos recordarte que tu préstamo del dispositivo <strong>${numeroSerieDispositivo}</strong> está a punto de caducar.</p>
                    <h3>Detalles del préstamo:</h3>
                    <ul>
                        <li><strong>Dispositivo:</strong> ${numeroSerieDispositivo}</li>
                        <li><strong>Fecha de Devolución:</strong> ${fechaDevolucion}</li>
                    </ul>
                    <p>Por favor, asegúrate de devolver el dispositivo en el plazo establecido para evitar posibles sanciones. Si ya lo has devuelto, por favor, ignora este mensaje.</p>
                </div>
                <div class="footer">
                    Este es un correo electrónico automático. Por favor, no respondas a este mensaje.
                </div>
            </body>
            </html>
        """.trimIndent()

        helper.setText(htmlContent, true)

        try {
            mailSender.send(mimeMessage)
            println("Correo electrónico HTML de recordatorio de préstamo a punto de caducar enviado a: $to")
        } catch (e: Exception) {
            println("Error al enviar el correo electrónico HTML de recordatorio de préstamo a punto de caducar a $to: ${e.message}")
        }
    }

    fun sendHtmlEmailPrestamoCaducado(to: String, subject: String, nombreUsuario: String, numeroSerieDispositivo: String, fechaCaducidad: String) {
        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

        helper.setFrom(senderEmail)
        helper.setTo(to)
        helper.setSubject(subject)

        val htmlContent = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>$subject</title>
                <style>
                    body { font-family: sans-serif; }
                    .header { background-color: #DC143C; color: #fff; padding: 20px; text-align: center; } /* Rojo para caducado */
                    .content { padding: 20px; }
                    .footer { margin-top: 20px; font-size: 0.8em; color: #14124f; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>LoanTech</h1>
                </div>
                <div class="content">
                    <h3>¡Tu Préstamo ha Caducado!</h3>
                    <p>Estimado/a <strong>${nombreUsuario}</strong>,</p>
                    <p>Te informamos que tu préstamo del dispositivo <strong>${numeroSerieDispositivo}</strong> ha caducado en la fecha <strong>${fechaCaducidad}</strong>.</p>
                    <h3>Detalles del préstamo:</h3>
                    <ul>
                        <li><strong>Dispositivo:</strong> ${numeroSerieDispositivo}</li>
                        <li><strong>Fecha de Caducidad:</strong> ${fechaCaducidad}</li>
                    </ul>
                    <p>Es imprescindible que devuelvas el dispositivo a la mayor brevedad posible para evitar la aplicación de posibles sanciones según nuestras políticas. Por favor, contacta con el departamento de informática.</p>
                </div>
                <div class="footer">
                    Este es un correo electrónico automático. Por favor, no respondas a este mensaje.
                </div>
            </body>
            </html>
        """.trimIndent()

        helper.setText(htmlContent, true)

        try {
            mailSender.send(mimeMessage)
            println("Correo electrónico HTML de préstamo caducado enviado a: $to")
        } catch (e: Exception) {
            println("Error al enviar el correo electrónico HTML de préstamo caducado a $to: ${e.message}")
        }
    }

    fun sendHtmlEmailUsuarioReactivado(to: String, subject: String, nombreUsuario: String) {
        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

        helper.setFrom(senderEmail)
        helper.setTo(to)
        helper.setSubject(subject)

        val htmlContent = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>$subject</title>
                <style>
                    body { font-family: sans-serif; }
                    .header { background-color: #28a745; color: #fff; padding: 20px; text-align: center; } /* Verde para reactivación */
                    .content { padding: 20px; }
                    .footer { margin-top: 20px; font-size: 0.8em; color: #14124f; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>LoanTech</h1>
                </div>
                <div class="content">
                    <h3>¡Tu Cuenta Ha Sido Reactivada!</h3>
                    <p>Estimado/a <strong>${nombreUsuario}</strong>,</p>
                    <p>Nos complace informarte que tu cuenta en LoanTech ha sido reactivada exitosamente. Ahora puedes volver a utilizar nuestros servicios con normalidad.</p>
                    <p>Si tienes alguna pregunta o necesitas ayuda, acércate al departamento de informática.</p>
                </div>
                <div class="footer">
                    Este es un correo electrónico automático. Por favor, no respondas a este mensaje.
                </div>
            </body>
            </html>
        """.trimIndent()

        helper.setText(htmlContent, true)

        try {
            mailSender.send(mimeMessage)
            println("Correo electrónico HTML de reactivación de usuario enviado a: $to")
        } catch (e: Exception) {
            println("Error al enviar el correo electrónico HTML de reactivación de usuario a $to: ${e.message}")
        }
    }
}