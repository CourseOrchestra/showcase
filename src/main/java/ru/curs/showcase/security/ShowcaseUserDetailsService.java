package ru.curs.showcase.security;

import java.util.Collection;

import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;

public class ShowcaseUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String arg0) throws UsernameNotFoundException {
		String username = "";
		String password = "";
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			username = auth.getPrincipal().toString();
			Object pwd = auth.getCredentials();
			password = (String) pwd;
			@SuppressWarnings("unchecked")
			Collection<GrantedAuthority> ga = (Collection<GrantedAuthority>) auth.getAuthorities();
			UserDetails user = new User(username, password, true, true, true, true, ga);
			return user;
		}
		return null;
	}

}
