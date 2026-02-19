import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOriginPatterns(
                    "https://*.vercel.app"   // allows all Vercel deployments
            )
            .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
            .allowedHeaders("*");
  }
}
