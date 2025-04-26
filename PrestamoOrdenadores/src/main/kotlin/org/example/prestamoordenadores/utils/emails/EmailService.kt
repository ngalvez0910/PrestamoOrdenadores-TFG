package org.example.prestamoordenadores.utils.emails

import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(private val mailSender: JavaMailSender) {

    @Value("\${spring.mail.username}")
    private lateinit var senderEmail: String

    fun sendHtmlEmail(to: String, subject: String, nombreUsuario: String, numeroSerieDispositivo: String, fechaDevolucion: String, pdfBytes: ByteArray, nombreArchivoPdf: String) {
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
                    <p>Hola <strong>${nombreUsuario}</strong>,</p>
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
}