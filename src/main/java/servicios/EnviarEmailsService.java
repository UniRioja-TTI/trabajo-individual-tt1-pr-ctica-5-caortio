package servicios;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import interfaces.InterfazEnviarEmails;
import modelo.Destinatario;
@Service
public class EnviarEmailsService implements InterfazEnviarEmails {

	private static Logger logger = LoggerFactory.getLogger(EnviarEmailsService.class);
	
	@Override
	public boolean enviarEmail(Destinatario dest, String email) {
		if (dest == null || email == null || email.trim().isEmpty()) {
            logger.warn("Intento de enviar email inválido: destinatario o contenido nulo/vacío");
            return false;
        }

        logger.info("──────────────────────────────────────────────");
        logger.info("EMAIL SIMULADO - ENVIADO EXITOSAMENTE");
        logger.info("Para........: {}", dest);  
        logger.info("Asunto......: (no implementado aún)");
        logger.info("Contenido...: {}", email);
        logger.info("──────────────────────────────────────────────");

        return true;
	}

}
 