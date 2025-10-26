package com.app.financiera.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.app.financiera.entity.Usuario;
import com.app.financiera.entity.AportePension;
import com.app.financiera.entity.Seguro;
import com.app.financiera.repository.UsuarioRepository;
import com.app.financiera.repository.SaldoPensionRepository;
import com.app.financiera.repository.AportePensionRepository;
import com.app.financiera.repository.SeguroRepository;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

@Service
public class ChatBotServiceImpl implements ChatBotService {

    private static final Logger logger = LoggerFactory.getLogger(ChatBotServiceImpl.class);

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SaldoPensionRepository saldoPensionRepository;

    @Autowired
    private AportePensionRepository aportePensionRepository;

    @Autowired
    private SeguroRepository seguroRepository;

    @Autowired
    private EmailService emailService;

    private static final String PROMPT_SISTEMA = """
        Eres un asistente virtual experto en pensiones y seguros para SumaqSeguros.
        
        INSTRUCCIONES IMPORTANTES:
        - Responde SOLO sobre: pensiones (ONP, AFP), seguros, aportes, saldos y proyecciones
        - NO generes contenido externo a estos temas
        - Respuestas BREVES y DIRECTAS (máximo 3-4 líneas)
        - Si no puedes responder, sugiere contactar a un asesor humano
        - NO inventes datos, usa solo información proporcionada
        - Sé amable y profesional
        
        TEMAS QUE PUEDES RESPONDER:
        ✓ ¿Qué es ONP/AFP?
        ✓ Diferencias entre ONP y AFP
        ✓ Cómo consultar aportes
        ✓ Información sobre saldos y proyecciones
        ✓ Tipos de seguros disponibles
        ✓ Cómo registrar aportes
        ✓ Preguntas sobre pólizas
        
        Si el usuario pregunta algo fuera de estos temas, responde:
        "Lo siento, solo puedo ayudarte con consultas sobre pensiones y seguros. ¿Necesitas hablar con un asesor?"
        """;

    @Override
    public String procesarMensaje(String mensaje, Integer idUsuario) {
        try {
            String contextoUsuario = obtenerContextoUsuario(idUsuario);

            String promptCompleto = PROMPT_SISTEMA + "\n\n" +
                    "CONTEXTO DEL USUARIO:\n" + contextoUsuario + "\n\n" +
                    "PREGUNTA DEL USUARIO: " + mensaje;

            System.out.println("GOOGLE_API_KEY: " + geminiApiKey);

            Client client = new Client();

            GenerateContentResponse response =
                    client.models.generateContent("gemini-2.5-flash", promptCompleto, null);

            String respuesta = response.text();
            if (respuesta == null || respuesta.isEmpty()) {
                respuesta = generarRespuestaBasica(mensaje, contextoUsuario);
            }

            return respuesta;

        } catch (Exception e) {
            logger.error("Error generando respuesta con Gemini: {}", e.getMessage());
            return "Lo siento, ocurrió un error al generar la respuesta.";
        }
    }


    @Override
    public String obtenerContextoUsuario(Integer idUsuario) {
        StringBuilder contexto = new StringBuilder();

        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();

                contexto.append("- Régimen: ").append(usuario.getTipoRegimen() != null ? usuario.getTipoRegimen() : "No especificado").append("\n");
                contexto.append("- AFP: ").append(usuario.getAfp() != null ? usuario.getAfp().getNombre() : "No afiliado").append("\n");

                // Saldo
                Double saldoTotal = saldoPensionRepository.sumSaldosUsuario(idUsuario);
                if (saldoTotal != null && saldoTotal > 0) {
                    contexto.append("- Saldo total: S/ ").append(String.format("%.2f", saldoTotal)).append("\n");
                }

                // Aportes
                List<AportePension> aportes = aportePensionRepository.findByUsuarioId(idUsuario);
                if (!aportes.isEmpty()) {
                    contexto.append("- Total aportes registrados: ").append(aportes.size()).append("\n");
                }

                // Seguros
                List<Seguro> seguros = seguroRepository.findByUsuario(idUsuario);
                if (!seguros.isEmpty()) {
                    long segurosActivos = seguros.stream()
                            .filter(s -> "Activo".equals(s.getEstado()) || "Vigente".equals(s.getEstado()))
                            .count();
                    contexto.append("- Seguros activos: ").append(segurosActivos).append("\n");
                }
            }
        } catch (Exception e) {
            logger.error("Error obteniendo contexto: {}", e.getMessage());
        }

        return contexto.length() > 0 ? contexto.toString() : "Usuario sin datos registrados";
    }

    @Override
    public boolean solicitarContactoAsesor(Integer idUsuario, String motivo) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();

                // Enviar email al equipo de soporte
                String asunto = "Solicitud de Asesoría - ChatBot";
                String mensaje = String.format("""
                    Un usuario ha solicitado contacto con un asesor desde el ChatBot.
                    
                    DATOS DEL USUARIO:
                    - Nombre: %s %s
                    - DNI: %s
                    - Email: %s
                    - Teléfono: %s
                    
                    MOTIVO:
                    %s
                    
                    Por favor, contactar al usuario a la brevedad.
                    """,
                        usuario.getNombre(),
                        usuario.getApellido(),
                        usuario.getDni(),
                        usuario.getCorreo(),
                        usuario.getTelefono(),
                        motivo != null ? motivo : "No especificado"
                );

                emailService.enviarCorreoSimple("soporte@sumaqseguros.com", asunto, mensaje);

                // Notificar al usuario
                if (usuario.isNotificacionesEmail()) {
                    emailService.enviarCorreoSimple(
                            usuario.getCorreo(),
                            "Solicitud de Asesoría Recibida",
                            String.format("""
                            Hola %s,
                            
                            Hemos recibido tu solicitud de asesoría.
                            Un asesor se comunicará contigo pronto por correo o teléfono.
                            
                            Gracias por confiar en SumaqSeguros.
                            """, usuario.getNombre())
                    );
                }

                logger.info("Solicitud de asesor enviada para usuario: {}", idUsuario);
                return true;
            }

            return false;

        } catch (Exception e) {
            logger.error("Error solicitando asesor: {}", e.getMessage());
            return false;
        }
    }

    private String generarRespuestaBasica(String mensaje, String contextoUsuario) {
        String mensajeLower = mensaje.toLowerCase();

        if (mensajeLower.contains("onp") && mensajeLower.contains("afp")) {
            return "ONP es un sistema público de pensiones con aporte solidario del 13%. AFP es privado con cuentas individuales y diferentes fondos de inversión. ¿Necesitas más detalles?";
        }

        if (mensajeLower.contains("saldo") || mensajeLower.contains("cuanto tengo")) {
            return "Puedes consultar tu saldo en la sección 'Panel Financiero' o 'Gestión de Pensiones'. También puedes ver proyecciones de pensión. ¿Necesitas ayuda navegando?";
        }

        if (mensajeLower.contains("aporte") || mensajeLower.contains("registrar")) {
            return "Puedes registrar aportes en 'Gestión de Pensiones'. Necesitas: período, monto, empleador y fecha. ¿Quieres que te guíe paso a paso?";
        }

        if (mensajeLower.contains("seguro")) {
            return "Ofrecemos seguros vehiculares, de hogar, salud y vida. Puedes compararlos en la sección 'Comparador'. ¿Te interesa alguno en particular?";
        }

        return "Puedo ayudarte con consultas sobre pensiones (ONP/AFP), aportes, saldos y seguros. ¿Sobre qué necesitas información?";
    }
}