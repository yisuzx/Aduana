package com.Sistema.Aduana.Service;

import com.Sistema.Aduana.Entity.Contacto;
import com.Sistema.Aduana.Repository.ContactoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactoService {
    private final ContactoRepository contactoRepository;

    public Contacto save(Contacto contacto) {
        return contactoRepository.save(contacto);
    }

    public List<Contacto> findAll() {
        return contactoRepository.findAll();
    }

    public Contacto findById(Long id) {
        return contactoRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        contactoRepository.deleteById(id);
    }

    public Contacto responderContacto(Long id, String respuesta) {
        Contacto contacto = findById(id);
        if (contacto != null) {
            contacto.setRespuesta(respuesta);
            contacto.setEstado("RESPONDIDO");
            contacto.setFechaRespuesta(java.time.LocalDateTime.now());
            return save(contacto);
        }
        return null;
    }
}