
package com.ecommerce.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecommerce.model.Orden;
import com.ecommerce.model.Usuario;
import com.ecommerce.service.IOrdenService;
import com.ecommerce.service.IUsuarioService;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
	
	private final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordenService;
	
	BCryptPasswordEncoder passEncode = new BCryptPasswordEncoder();
	
	@GetMapping("/registro")
	public String create() {	
		return"usuario/registro";
	}
	
	
	@PostMapping("/save")
	public String save(Usuario usuario) {
		logger.info("Usuario Registro{}", usuario );
		usuario.setTipo("USER");
		usuario.setPassword(passEncode.encode(usuario.getPassword()));	 //encriptacion de password	
		usuarioService.save(usuario);
		
		return"redirect:/";
	}
	
	
	@GetMapping("/login")
	public String login() {
		return"usuario/login";
	}
	
	//se cambio para la seguridad de Spring - https://www.youtube.com/watch?v=w-0e8fLmd-o
	/*@PostMapping("/acceder")*/ 
	@GetMapping("/acceder")
	public String acceder(Usuario usuario, HttpSession session) {
		logger.info("Accesos: {}", usuario);
		
		/*Optional<Usuario> user=usuarioService.findByEmail(usuario.getEmail());*/ //se cambio por la seguridad de Spring
		Optional<Usuario> user=usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString()));
//		logger.info("Usuario obtenido de DB {}", user.get());
		
		if(user.isPresent()) {
			session.setAttribute("idusuario",user.get().getId());
			if(user.get().getTipo().equals("ADMIN")) {
				//System.out.println("administrador");
				//logger.info("Usuario obtenido de DB {}", user.get());
				logger.info("Sesion del Usuaro {}", session.getAttribute("idusuario"));
				return"redirect:/administrador/";		
			}else {
				return"redirect:/";
			}
		}else {
			logger.info("Usuario no existe");
		}
		
		return "redirect:/";
		
	}
	
	@GetMapping("/compras")
	public String obtenerCompras(HttpSession session, Model model) {
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		
		Usuario usuario = usuarioService.findById(   Integer.parseInt(session.getAttribute("idusuario").toString())).get();
		List<Orden>ordenes = ordenService.findByUsuario(usuario);
		model.addAttribute("ordenes", ordenes);
		
		return "usuario/compras";
	}
	

	@GetMapping("/detalle/{id}")
	public String detalleCompra(@PathVariable Integer id, HttpSession session, Model model) {
		logger.info("Id de la orden: {}", id);
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		Optional <Orden> orden = ordenService.findById(id);
		model.addAttribute("detalles", orden.get().getDetalle());
		
		return"usuario/detallecompra";
	}
	
	@GetMapping("/cerrar")
	public String cerrarSesion(HttpSession session) {
		session.removeAttribute("idusuario");
	return"redirect:/";	
	}
}

