package hide92795.novelengine.filecreator;

import hide92795.novelengine.filecreator.saver.SaverBasic;
import hide92795.novelengine.filecreator.saver.SaverBox;
import hide92795.novelengine.filecreator.saver.SaverButton;
import hide92795.novelengine.filecreator.saver.SaverCharacter;
import hide92795.novelengine.filecreator.saver.SaverFigure;
import hide92795.novelengine.filecreator.saver.SaverFont;
import hide92795.novelengine.filecreator.saver.SaverGui;
import hide92795.novelengine.filecreator.saver.SaverImage;
import hide92795.novelengine.filecreator.saver.SaverSound;
import hide92795.novelengine.filecreator.saver.SaverStory;
import hide92795.novelengine.filecreator.saver.SaverVoice;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Set;

public class NovelEngineFileCreator {
	private static final String VERSION = "a1.5.0";
	private Properties project;
	private File output;
	private Properties cryptProp;
	/**
	 * プロジェクトのデータがあるディレクトリ
	 */
	private File path;
	private String encoding;


	public NovelEngineFileCreator() {
		System.out.println("----------------------------------------");
		System.out.println(" NovelEngine FileCreator v" + VERSION);
		System.out.println("----------------------------------------");
		System.out.println();
	}

	private void init() {
		VarNumManager.CHAPTER_ID.getMap().put("start", SaverStory.CHAPTER_START);
		VarNumManager.CHAPTER_ID.getMap().put("menu", SaverStory.CHAPTER_MENU);
	}

	private void loadProject(String file) throws FileNotFoundException, IOException {
		project = new Properties();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		project.load(reader);
		path = new File(project.getProperty("Path"));
		encoding = project.getProperty("Encoding", "UTF-8");
		cryptProp = new Properties();
		BufferedReader cryptReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path,
				"Crypt.cfg")), encoding));
		cryptProp.load(cryptReader);
		// System.out.println("変数用テーブルを作成します。");
		// TimeManager.start();
		// int num = 100000;
		// LinkedList<Integer> random = new LinkedList<Integer>();
		// for (int i = 0; i < num; i++) {
		// random.add(i);
		// }
		// TimeManager.end("完了：(", "sec)");
		// String randomValue = project.getProperty("VauleList");
		// if (randomValue == null) {
		// System.out.println("変数リストが存在しないので新しく作成します。");
		// }else{
		// System.out.println("既に存在する変数リストを読み込みます");
		// }
		System.out.println("Project Name : " + project.getProperty("Gamename"));
		System.out.println("     Version : " + project.getProperty("Version"));
		System.out.println();
		reader.close();
		cryptReader.close();
	}

	private void initFolder() throws IOException, InterruptedException {
		delete(new File("output"));
		Thread.sleep(500);
		output = new File("output");
		output.mkdir();
		new File(output, "img").mkdir();
		new File(output, "sound").mkdir();
		new File(output, "object").mkdir();
		new File(output, "story").mkdir();
		new File(output, "voice").mkdir();
		Thread.sleep(500);
	}

	private void createBasicData() throws Exception {
		SaverBasic basic = new SaverBasic(project, output, cryptProp, path, encoding);
		basic.pack();

		File startFile = new File(path, "start.txt");
		File menuFile = new File(path, "menu.txt");

		System.out.println(startFile.getName());
		SaverStory saverStorymain = new SaverStory(startFile, output, cryptProp, encoding);
		saverStorymain.pack();

		System.out.println(menuFile.getName());
		SaverStory saverStorymenu = new SaverStory(menuFile, output, cryptProp, encoding);
		saverStorymenu.pack();
	}

	public static void main(String[] args) {
		String name;
		if (args.length == 0) {
			name = "example.project";
		} else {
			name = args[0];
		}

		try {
			NovelEngineFileCreator novelEngineFileCreator = new NovelEngineFileCreator();
			novelEngineFileCreator.loadProject(name);
			novelEngineFileCreator.init();

			System.out.println("Creating output folder...");
			TimeManager.start();
			novelEngineFileCreator.initFolder();
			TimeManager.end("Complete. (", "sec)");
			System.out.println();

			System.out.println("Creating basic data...");
			TimeManager.start();
			novelEngineFileCreator.createBasicData();
			TimeManager.end("Complete. (", "sec)");
			System.out.println();

			System.out.println("Creating box data...");
			TimeManager.start();
			novelEngineFileCreator.createBoxData();
			TimeManager.end("Complete. (", "sec)");
			System.out.println();

			System.out.println("Creating font data...");
			TimeManager.start();
			novelEngineFileCreator.createFontData();
			TimeManager.end("Complete. (", "sec)");
			System.out.println();

			System.out.println("Creating button data...");
			TimeManager.start();
			novelEngineFileCreator.createButtonData();
			TimeManager.end("Complete. (", "sec)");
			System.out.println();

			System.out.println("Creating gui data...");
			TimeManager.start();
			novelEngineFileCreator.createGuiData();
			TimeManager.end("Complete. (", "sec)");
			System.out.println();

			System.out.println("Creating story data...");
			TimeManager.start();
			novelEngineFileCreator.createStoryData();
			TimeManager.end("Complete. (", "sec)");
			System.out.println();

			System.out.println("Creating figure data...");
			TimeManager.start();
			novelEngineFileCreator.createFigireData();
			TimeManager.end("Complete. (", "sec)");
			System.out.println();

			System.out.println("Creating chracter data...");
			TimeManager.start();
			novelEngineFileCreator.createCharacterData();
			TimeManager.end("Complete. (", "sec)");
			System.out.println();

			System.out.println("Creating image data...");
			TimeManager.start();
			novelEngineFileCreator.createImageData();
			TimeManager.end("Complete. (", "sec)");
			System.out.println();

			System.out.println("Creating sound data...");
			TimeManager.start();
			novelEngineFileCreator.createSoundData();
			TimeManager.end("Complete. (", "sec)");
			System.out.println();

			System.out.println("Creating voice data...");
			TimeManager.start();
			novelEngineFileCreator.createVoiceData();
			TimeManager.end("Complete. (", "sec)");
			System.out.println();

			System.out.println("Finish all process!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createFigireData() throws Exception {
		File outputDir = new File(output, "object");
		File root = new File(path, "Figure");
		SaverFigure saver = new SaverFigure(outputDir, cryptProp, encoding, root);
		saver.pack();
	}

	private void createVoiceData() throws Exception {
		File outputDir = new File(output, "voice");
		File root = new File(path, "Voice");
		SaverVoice saver = new SaverVoice(outputDir, cryptProp, root, encoding);
		saver.pack();
	}

	private void createFontData() throws Exception {
		File outputDir = new File(output, "object");
		File root = new File(path, "Font");
		SaverFont saver = new SaverFont(outputDir, cryptProp, root, encoding);
		saver.pack();
	}

	private void createBoxData() throws Exception {
		File outputDir = new File(output, "object");
		File root = new File(path, "Box");
		SaverBox saver = new SaverBox(outputDir, cryptProp, root, encoding);
		saver.pack();
	}

	private void createCharacterData() throws Exception {
		File outputDir = new File(output, "object");
		File root = new File(path, "Character");
		SaverCharacter saver = new SaverCharacter(outputDir, cryptProp, root, encoding);
		saver.pack();
	}

	private void createGuiData() throws Exception {
		File outputDir = new File(output, "object");
		SaverGui saver = new SaverGui(outputDir, cryptProp, encoding);
		saver.pack();
	}

	private void createButtonData() throws Exception {
		File root = new File(path, "Button");
		File outputDir = new File(output, "object");
		SaverButton saver = new SaverButton(outputDir, cryptProp, root, encoding);
		saver.pack();
	}

	private void createSoundData() throws Exception {
		Set<String> audios = VarNumManager.SOUND.getMap().keySet();
		File root = new File(path, "Sound");
		File outputDir = new File(output, "sound");
		for (String audio : audios) {
			File f = new File(root, audio + ".ogg");
			if (!f.exists()) {
				throw new FileNotFoundException("オーディオファイル \"" + f.getName() + "\" が見つかりません。");
			}
			SaverSound saverSound = new SaverSound(outputDir, cryptProp, f, VarNumManager.SOUND.getMap().get(audio),
					encoding);
			saverSound.pack();
		}
	}

	private void createStoryData() throws Exception {
		File folder = new File(path, "Story");
		File outputDir = new File(output, "story");
		File[] files = folder.listFiles();
		for (File file : files) {
			VarNumManager.SCENE_ID.reset();
			System.out.println(file.getName());
			SaverStory saverStory = new SaverStory(file, outputDir, cryptProp, encoding);
			saverStory.pack();
		}
	}

	private void createImageData() throws Exception {
		Set<String> images = VarNumManager.IMAGE.getMap().keySet();
		File root = new File(path, "Image");
		File outputDir = new File(output, "img");
		for (String image : images) {
			File f = new File(root, image + ".png");
			if (!f.exists()) {
				throw new FileNotFoundException("イメージファイル \"" + f.getName() + "\" が見つかりません。");
			}
			SaverImage saverImage = new SaverImage(outputDir, cryptProp, f, VarNumManager.IMAGE.getMap().get(image),
					encoding);
			saverImage.pack();
		}
	}

	private void delete(File f) {
		if (f.exists() == false) {
			return;
		} else if (f.isFile()) {
			f.delete();
		} else if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				delete(files[i]);
			}
			f.delete();
		}
	}
}
