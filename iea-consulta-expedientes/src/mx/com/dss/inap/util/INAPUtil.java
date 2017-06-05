package mx.com.dss.inap.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class INAPUtil {

	private static Cipher cipher;
	private static Key key;

	private static void getCrypterKey() {
		KeyGenerator kgen;
		try {
			kgen = KeyGenerator.getInstance(INAPConstant.CRYPTO);
			kgen.init(128);
			byte[] keyBytes = INAPConstant.KEY_CRYPTO.getBytes();
			Key skeySpec = new SecretKeySpec(keyBytes, INAPConstant.CRYPTO);
			key = skeySpec;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	private static void setUpEncrypter() {
		try {
			getCrypterKey();
			cipher = Cipher.getInstance(INAPConstant.CRYPTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo para encriptar un mensaje
	 * @param message {@link String}
	 * @return cryptedMessage {@link String}
	 */
	public static String encryptedMessage(String message) {
		try {
			setUpEncrypter();
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] bytesMensaje = cipher.doFinal(message.getBytes());
			message = Base64.encodeBase64String(bytesMensaje);
		} catch (Exception e) {
			new Exception("Ocurrio un error al intentar encriptar el mensaje",
					e);
		}
		return message;
	}

	/**
	 * M�todo para desencriptar un mensaje
	 * @param cryptedMessage {@link String}
	 * @return message {@link String}
	 */
	public static String decryptedMessage(String cryptedMessage) {
		try {
			setUpEncrypter();
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] bytesMensaje = cipher.doFinal(cryptedMessage.getBytes());
			cryptedMessage = Base64.encodeBase64String(bytesMensaje);
		} catch (Exception e) {
			new Exception(
					"Ocurrio un error al intentar desencriptar el mensaje", e);
		}
		return cryptedMessage;
	}

	/**
	 * Metodo que carga el archivo de propiedades del sistema
	 * 
	 * @param pathFile
	 * @return
	 */
	public static Properties loadProperties(String pathFile) throws Exception {
		
		Properties properties = new Properties();
		
			File file = new File(pathFile);
			if (!file.exists())
				throw new Exception("No se encuentra el archivo properties a cargar: ["	+ pathFile + "]");
			InputStream inputStream = new FileInputStream(pathFile);
			
			properties.load(inputStream);
			
		return properties;
	}

	/**
	 * Metodo que convierte una fecha (String) a Date
	 * @param fecha {@link String}
	 * @return fecha {@link Date}
	 */
	public Date getDate(String fecha) {
		Date d = null;
		try {
			d = (new SimpleDateFormat("dd-MM-yyyy")).parse(fecha);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return d;
	}

	
    public static String getMappPathName(List<?> resultList)throws Exception{
        String path = "";
        for (Object docProp : resultList) {
			Properties properties = (Properties) docProp;
			path = properties.getProperty("PATHNAME", "");
		}
        return path;
    }
}
