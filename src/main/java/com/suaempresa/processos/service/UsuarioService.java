package com.suaempresa.processos.service;

import com.suaempresa.processos.model.Usuario;
import com.suaempresa.processos.repository.UsuarioRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class UsuarioService {

    @Inject
    private UsuarioRepository usuarioRepository;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(Integer id) {
        return usuarioRepository.findById(id);
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional
    public void save(Usuario usuario) {
        // Sempre salva senha como hash BCrypt
        if (usuario.getSenha() != null && !usuario.getSenha().isEmpty() &&
            !(usuario.getSenha().startsWith("$2a$") || usuario.getSenha().startsWith("$2b$") || usuario.getSenha().startsWith("$2y$"))) {
            String hash = org.mindrot.jbcrypt.BCrypt.hashpw(usuario.getSenha(), org.mindrot.jbcrypt.BCrypt.gensalt());
            usuario.setSenha(hash);
        }
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void delete(Usuario usuario) {
        usuarioRepository.delete(usuario);
    }


    public boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) return false;
        // Só autentica se o hash for BCrypt
        if (hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$") || hashedPassword.startsWith("$2y$")) {
            try {
                return BCrypt.checkpw(plainPassword, hashedPassword);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        // Não aceita mais senha em texto puro
        return false;
    }


    @SuppressWarnings("unused")
    private String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) return null;
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Autentica o usuário, aceita senha em texto puro (migração) e já migra para hash seguro após login.
     * Lança SecurityException em caso de erro.
     */
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            throw new SecurityException("Usuário ou senha inválidos");
        }
        String hash = usuario.getSenha();
        boolean isBCrypt = hash != null && (hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"));
        boolean autenticado = false;
        if (isBCrypt) {
            autenticado = checkPassword(senha, hash);
        } else {
            // Não aceita mais login com senha em texto puro
            throw new SecurityException("Usuário precisa redefinir a senha para acessar o sistema.");
        }
        if (!autenticado) {
            throw new SecurityException("Usuário ou senha inválidos");
        }
        return usuario;
    }
}


