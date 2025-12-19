package com.example.demo.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private UserService userService;

	public SecurityConfig(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	private OAuth2AuthorizedClientService authorizedClientService;

	@Bean
	// CORSの設定を行うメソッド
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// 許可するオリジン（フロントエンドのURL）を指定
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080"));

		// 許可するHTTPメソッドを指定（GET, POST, PUT, DELETE, OPTIONSを許可）
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

		// 許可するヘッダ情報を指定（authorizationとcontent-typeを許可）
		configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type"));

		// CORSの設定情報をURLベースで登録するためのオブジェクトを作成
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		// 全てのパスに対してCORSの設定を登録
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	@Order(1)
	SecurityFilterChain api(HttpSecurity http) throws Exception {
		http.securityMatcher("/api/**")
				.csrf(csrf -> csrf.disable())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(a -> a.anyRequest().authenticated())
				.addFilterBefore(
						new CookieAccessTokenFilter(),
						BearerTokenAuthenticationFilter.class)
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
		return http.build();
	}

	/**
	 * @param http
	 * @return
	 * @throws Exception
	 */
	@Bean
	@Order(2)
	SecurityFilterChain web(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(a -> a
				.requestMatchers("/login/**", "/oauth2/**", "/error").permitAll()
				.anyRequest().authenticated())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
				.oauth2Login(o -> o.successHandler(this::handleOAuth2LoginSuccess));
		return http.build();
	}

	private void handleOAuth2LoginSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {

		OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

		OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
				oauthToken.getAuthorizedClientRegistrationId(),
				oauthToken.getName());

		// ★ Access Token を取得
		String accessToken = client.getAccessToken().getTokenValue();

		Cookie cookie = new Cookie("ACCESS_TOKEN", accessToken);
		cookie.setHttpOnly(true); // ★ JS から触れない
		cookie.setSecure(false); // 本番では true（HTTPS）
		cookie.setPath("/");
		cookie.setMaxAge(60 * 60); // 1時間

		response.addCookie(cookie);

		DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
		// 認証に成功したユーザ情報を取得
		// クレーム（ユーザ属性）情報を取得
		// 基本的にはIDトークンから取得される
		// 利用可能な場合、userInfoエンドポイントにもアクセスして追加の情報を取得する
		Map<String, Object> attributes = oidcUser.getClaims();

		// クレーム情報から名前とメールアドレスを取得
		String username = (String) attributes.get("name");
		String email = (String) attributes.get("email");

		if (userService.findUserByEmail(email) == null) {
			// ユーザ情報を業務DBに保存し、ユーザIDを取得
			userService.SaveUser(username, email);
		}
		// HTTPステータスコードを200 OKに設定
		response.sendRedirect("/tasks");
	}

	public class CookieAccessTokenFilter extends OncePerRequestFilter {

		@Override
		protected void doFilterInternal(
				HttpServletRequest request,
				HttpServletResponse response,
				FilterChain filterChain) throws ServletException, IOException {

			if (request.getCookies() != null) {
				for (Cookie cookie : request.getCookies()) {
					if ("ACCESS_TOKEN".equals(cookie.getName())) {

						HttpServletRequest wrapped = new HttpServletRequestWrapper(request) {
							@Override
							public String getHeader(String name) {
								if ("Authorization".equalsIgnoreCase(name)) {
									return "Bearer " + cookie.getValue();
								}
								return super.getHeader(name);
							}
						};

						filterChain.doFilter(wrapped, response);
						return;
					}
				}
			}

			filterChain.doFilter(request, response);
		}
	}

}
