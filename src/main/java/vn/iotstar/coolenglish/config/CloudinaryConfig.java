package vn.iotstar.coolenglish.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

public class CloudinaryConfig {
    private static Cloudinary instance;

    static {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        
        String cloudName = dotenv.get("CLOUDINARY_CLOUD_NAME");
        String apiKey = dotenv.get("CLOUDINARY_API_KEY");
        String apiSecret = dotenv.get("CLOUDINARY_API_SECRET");

        if (cloudName == null || apiKey == null || apiSecret == null) {
            throw new RuntimeException(
                "Cloudinary configuration not found. Please set CLOUDINARY_CLOUD_NAME, "
                + "CLOUDINARY_API_KEY, and CLOUDINARY_API_SECRET in .env file"
            );
        }

        instance = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret
        ));
    }

    public static Cloudinary getInstance() {
        return instance;
    }

    private CloudinaryConfig() {
        // Private constructor to prevent instantiation
    }
}

