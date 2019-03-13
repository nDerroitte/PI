package cryptodecrtyptoGreg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Decrypt {
	public static void translate(File file, AES encrypter) throws IOException, EncryptionException{
		BufferedReader reader;
		reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();
		while (line != null) {
		
			String encrypted_data=line;
			System.out.println("Decrypted data :"+encrypter.decrypt(encrypted_data));
			line = reader.readLine();
		}
		reader.close();
	}
}
