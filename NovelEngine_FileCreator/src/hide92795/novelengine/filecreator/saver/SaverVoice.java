package hide92795.novelengine.filecreator.saver;

import hide92795.novelengine.filecreator.VarNumManager;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import javax.crypto.CipherOutputStream;

public class SaverVoice extends Saver {
	private File path;

	public SaverVoice(File output, Properties crypt, File path) {
		super(output, crypt);
		this.path = path;
	}

	@Override
	public void pack() throws Exception {
		HashMap<String, Integer> voices = VarNumManager.VOICE.getMap();
		Set<String> voice_s = voices.keySet();

		HashMap<String, File> inputDirs = new HashMap<String, File>();
		HashMap<String, File> outputDirs = new HashMap<String, File>();

		for (String voice : voice_s) {
			int id = voices.get(voice);
			String[] voiceData = voice.split("\\.", 2);
			String characterName = voiceData[0];
			String voiceName = voiceData[1];
			File rawDataDir;
			if (inputDirs.containsKey(characterName)) {
				rawDataDir = inputDirs.get(characterName);
			} else {
				rawDataDir = new File(path, characterName);
				inputDirs.put(characterName, rawDataDir);
				int characterId = VarNumManager.CHARACTER.add(characterName);
				File outPutDir = new File(output, String.valueOf(characterId));
				if (!outPutDir.exists()) {
					outPutDir.mkdir();
				}
				outputDirs.put(characterName, outPutDir);
			}
			File src = new File(rawDataDir, voiceName + ".ogg");

			System.out.println(characterName + ":" + src.getName());
			FileInputStream fis = new FileInputStream(src);

			File outputDir = outputDirs.get(characterName);
			CipherOutputStream cos = createCipherInputStream(new File(outputDir, id + ".nev"), crypt);

			byte[] a = new byte[8];
			int i = fis.read(a);

			while (i != -1) {
				cos.write(a, 0, i);
				i = fis.read(a);
			}

			cos.flush();
			cos.close();
			fis.close();
		}
	}
}