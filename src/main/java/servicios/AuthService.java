package servicios;

import DAO.DuenioRestauranteDAO;
import DAO.UsuarioDAO;
import entidades.Comensal;
import entidades.DuenioRestaurante;
import entidades.Restaurante;
import entidades.Usuario;
import exceptions.ServiceException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

public class AuthService {
    private final UsuarioDAO usuarioDAO;
    private final DuenioRestauranteDAO duenioRestauranteDAO;
    private static final int SALT_LENGTH = 16; // 16 bytes

    public AuthService(UsuarioDAO usuarioDAO, DuenioRestauranteDAO duenioDAO) {
        this.usuarioDAO = usuarioDAO;
        this.duenioRestauranteDAO = duenioDAO;
    }

    // Método para obtener todos los restaurantes
    public List<Restaurante> obtenerTodosRestaurantes() {
        return usuarioDAO.obtenerTodosRestaurantes();
    }

    // Genera un salt aleatorio seguro
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Hashea la contraseña con el salt proporcionado
    private String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(Base64.getDecoder().decode(salt));
            byte[] hashBytes = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear la contraseña", e);
        }
    }

    // Método para validar credenciales de usuario
    public boolean validarContrasena(String contrasenaPlana, String contrasenaAlmacenada) {
        if (contrasenaAlmacenada == null || !contrasenaAlmacenada.contains(":")) {
            // Para contraseñas antiguas sin hash
            return contrasenaPlana.equals(contrasenaAlmacenada);
        }

        String[] parts = contrasenaAlmacenada.split(":");
        if (parts.length != 2) return false;

        String salt = parts[0];
        String storedHash = parts[1];
        String computedHash = hashPassword(contrasenaPlana, salt);

        return computedHash.equals(storedHash);
    }

    // Método principal de login
    public Usuario login(String nombreUsuario, String contrasena) throws ServiceException {
        Usuario usuario = usuarioDAO.findByNombreUsuario(nombreUsuario);

        if (usuario == null) {
            throw new ServiceException("Credenciales inválidas");
        }

        // Verificación de contraseña
        if (!validarContrasena(contrasena, usuario.getContrasena())) {
            throw new ServiceException("Credenciales inválidas");
        }

        // Migrar contraseña antigua a nuevo formato si es necesario
        if (!usuario.getContrasena().contains(":")) {
            String salt = generateSalt();
            String newHashedPassword = hashPassword(contrasena, salt);
            usuario.setContrasena(salt + ":" + newHashedPassword);
            usuarioDAO.save(usuario);
        }

        return usuario;
    }

    // Registro de dueño de restaurante
    public void registrarDuenioRestaurante(DuenioRestaurante duenio) throws ServiceException {
        if (usuarioDAO.findByNombreUsuario(duenio.getNombreUsuario()) != null) {
            throw new ServiceException("El nombre de usuario ya existe");
        }

        // Hashear la contraseña antes de guardar
        String salt = generateSalt();
        String hashedPassword = hashPassword(duenio.getContrasena(), salt);
        duenio.setContrasena(salt + ":" + hashedPassword);

        duenioRestauranteDAO.save(duenio);
    }

    // Registro de comensal
    public void registrarComensal(Usuario usuario, String tipoComidaFavorita) throws ServiceException {
        if (usuarioDAO.findByNombreUsuario(usuario.getNombreUsuario()) != null) {
            throw new ServiceException("El nombre de usuario ya existe");
        }

        Comensal comensal = new Comensal();
        comensal.setNombreUsuario(usuario.getNombreUsuario());

        // Hashear la contraseña antes de guardar
        String salt = generateSalt();
        String hashedPassword = hashPassword(usuario.getContrasena(), salt);
        comensal.setContrasena(salt + ":" + hashedPassword);

        comensal.setEmail(usuario.getEmail());
        comensal.setTipoComidaFavorita(tipoComidaFavorita);
        comensal.setTipoUsuario("COMENSAL");

        usuarioDAO.save(comensal);
    }


    public boolean usuarioExiste(String nombreUsuario) {
        return usuarioDAO.findByNombreUsuario(nombreUsuario) != null;
    }


    public Usuario findByNombreUsuario(String nombreUsuario) {
        return usuarioDAO.findByNombreUsuario(nombreUsuario);
    }

    // Método para migrar contraseñas antiguas
    public void migrarContrasenasAntiguas() {
        List<Usuario> usuarios = usuarioDAO.findAll();
        for (Usuario usuario : usuarios) {
            if (usuario.getContrasena() != null && !usuario.getContrasena().contains(":")) {
                String salt = generateSalt();
                String hashedPassword = hashPassword(usuario.getContrasena(), salt);
                usuario.setContrasena(salt + ":" + hashedPassword);
                usuarioDAO.save(usuario);
            }
        }
    }
}
