//
// NovelEngine Project
//
// Copyright (C) 2013 - hide92795
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
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

/**
 * Clientで実行するためのデータをパッケージングするFileCreatorシステムの中枢クラスです。
 * 
 * @author hide92795
 */
public class NovelEngineFileCreator {
	/**
	 * FileCreatorのバージョンを表します。
	 */
	private static final String VERSION = "a1.6.1";
	/**
	 * FileCreatorシステムがデバッグモードで動作しているかどうかを表します。
	 */
	public static final boolean DEBUG = true;
	/**
	 * プロジェクトデータを表すプロパティです。
	 */
	private Properties project;
	/**
	 * 出力先のフォルダを表します。
	 */
	private File output;
	/**
	 * 暗号化に関する情報を保存するプロパティです。
	 */
	private Properties cryptProperties;
	/**
	 * プロジェクトのデータがあるディレクトリです。
	 */
	private File path;
	/**
	 * リソースの読み込み時に使用する文字コードです。
	 */
	private String encoding;


	/**
	 * FileCreatorシステムを生成します。
	 */
	public NovelEngineFileCreator() {
		System.out.println("----------------------------------------");
		System.out.println(" NovelEngine FileCreator v" + VERSION);
		System.out.println("----------------------------------------");
		System.out.println();
	}

	/**
	 * FileCreatorシステムを初期化します。
	 */
	private void init() {
		VarNumManager.CHAPTER_ID.getMap().put("start", SaverStory.CHAPTER_START);
		VarNumManager.CHAPTER_ID.getMap().put("menu", SaverStory.CHAPTER_MENU);
	}

	/**
	 * FileCreatorシステムを起動します。
	 * 
	 * @param args
	 *            引数
	 */
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

	/**
	 * プロジェクトファイルをロードします。
	 * 
	 * @param file
	 *            プロジェクトファイルのパス
	 * @throws IOException
	 *             何らかの入出力エラーが発生した場合
	 */
	private void loadProject(String file) throws IOException {
		project = new Properties();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		project.load(reader);
		path = new File(project.getProperty("Path"));
		encoding = project.getProperty("Encoding", "UTF-8");
		cryptProperties = new Properties();
		BufferedReader cryptReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path,
				"Crypt.cfg")), encoding));
		cryptProperties.load(cryptReader);
		System.out.println("Project Name : " + project.getProperty("Gamename"));
		System.out.println("     Version : " + project.getProperty("Version"));
		System.out.println();
		reader.close();
		cryptReader.close();
	}

	/**
	 * 出力先のフォルダを作成します。
	 * 
	 * @throws IOException
	 *             何らかの入出力エラーが発生した場合
	 * @throws InterruptedException
	 *             何らかのスレッドが現在のスレッドに割り込んだ場合。
	 */
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

	/**
	 * 基本データを作成・保存します。
	 * 
	 * @throws Exception
	 *             何らかのエラーが発生した場合
	 */
	private void createBasicData() throws Exception {
		SaverBasic basic = new SaverBasic(output, path, cryptProperties, encoding, project);
		basic.pack();

		File startFile = new File(path, "start.txt");
		File menuFile = new File(path, "menu.txt");

		System.out.println(startFile.getName());
		SaverStory saverStorymain = new SaverStory(output, startFile, cryptProperties, encoding);
		saverStorymain.pack();

		System.out.println(menuFile.getName());
		SaverStory saverStorymenu = new SaverStory(output, menuFile, cryptProperties, encoding);
		saverStorymenu.pack();
	}

	/**
	 * メッセージボックスのデータを作成・保存します。
	 * 
	 * @throws Exception
	 *             何らかのエラーが発生した場合
	 */
	private void createBoxData() throws Exception {
		File outputDir = new File(output, "object");
		File root = new File(path, "Box");
		SaverBox saver = new SaverBox(outputDir, root, cryptProperties, encoding);
		saver.pack();
	}

	/**
	 * フォントデータを作成・保存します。
	 * 
	 * @throws Exception
	 *             何らかのエラーが発生した場合
	 */
	private void createFontData() throws Exception {
		File outputDir = new File(output, "object");
		File root = new File(path, "Font");
		SaverFont saver = new SaverFont(outputDir, root, cryptProperties, encoding);
		saver.pack();
	}

	/**
	 * ボタンデータを作成・保存します。
	 * 
	 * @throws Exception
	 *             何らかのエラーが発生した場合
	 */
	private void createButtonData() throws Exception {
		File root = new File(path, "Button");
		File outputDir = new File(output, "object");
		SaverButton saver = new SaverButton(outputDir, root, cryptProperties, encoding);
		saver.pack();
	}

	/**
	 * GUIデータを作成・保存します。
	 * 
	 * @throws Exception
	 *             何らかのエラーが発生した場合
	 */
	private void createGuiData() throws Exception {
		File outputDir = new File(output, "object");
		SaverGui saver = new SaverGui(outputDir, cryptProperties, encoding);
		saver.pack();
	}

	/**
	 * ストーリーデータを作成・保存します。
	 * 
	 * @throws Exception
	 *             何らかのエラーが発生した場合
	 */
	private void createStoryData() throws Exception {
		File folder = new File(path, "Story");
		File outputDir = new File(output, "story");
		File[] files = folder.listFiles();
		for (File file : files) {
			VarNumManager.SCENE_ID.reset();
			System.out.println(file.getName());
			SaverStory saverStory = new SaverStory(outputDir, file, cryptProperties, encoding);
			saverStory.pack();
		}
	}

	/**
	 * フィギュアデータを作成・保存します。
	 * 
	 * @throws Exception
	 *             何らかのエラーが発生した場合
	 */
	private void createFigireData() throws Exception {
		File outputDir = new File(output, "object");
		File root = new File(path, "Figure");
		SaverFigure saver = new SaverFigure(outputDir, root, cryptProperties, encoding);
		saver.pack();
	}

	/**
	 * キャラクターデータを作成・保存します。
	 * 
	 * @throws Exception
	 *             何らかのエラーが発生した場合
	 */
	private void createCharacterData() throws Exception {
		File outputDir = new File(output, "object");
		File root = new File(path, "Character");
		SaverCharacter saver = new SaverCharacter(outputDir, root, cryptProperties, encoding);
		saver.pack();
	}

	/**
	 * 画像データを作成・保存します。
	 * 
	 * @throws Exception
	 *             何らかのエラーが発生した場合
	 */
	private void createImageData() throws Exception {
		Set<String> images = VarNumManager.IMAGE.getMap().keySet();
		File root = new File(path, "Image");
		File outputDir = new File(output, "img");
		for (String image : images) {
			File f = new File(root, image + ".png");
			if (!f.exists()) {
				throw new FileNotFoundException("イメージファイル \"" + f.getName() + "\" が見つかりません。");
			}
			SaverImage saverImage = new SaverImage(outputDir, f, cryptProperties, encoding, VarNumManager.IMAGE.getMap().get(
							image));
			saverImage.pack();
		}
	}

	/**
	 * サウンドデータを作成・保存します。
	 * 
	 * @throws Exception
	 *             何らかのエラーが発生した場合
	 */
	private void createSoundData() throws Exception {
		Set<String> audios = VarNumManager.SOUND.getMap().keySet();
		File root = new File(path, "Sound");
		File outputDir = new File(output, "sound");
		for (String audio : audios) {
			File f = new File(root, audio + ".ogg");
			if (!f.exists()) {
				throw new FileNotFoundException("オーディオファイル \"" + f.getName() + "\" が見つかりません。");
			}
			SaverSound saverSound = new SaverSound(outputDir, f, cryptProperties, encoding, VarNumManager.SOUND.getMap().get(
							audio));
			saverSound.pack();
		}
	}

	/**
	 * ボイスデータを作成・保存します。
	 * 
	 * @throws Exception
	 *             何らかのエラーが発生した場合
	 */
	private void createVoiceData() throws Exception {
		File outputDir = new File(output, "voice");
		File root = new File(path, "Voice");
		SaverVoice saver = new SaverVoice(outputDir, root, cryptProperties, encoding);
		saver.pack();
	}

	/**
	 * 指定したディレクトリをサブフォルダを含めて削除します。
	 * 
	 * @param f
	 *            削除するフォルダー
	 */
	private static void delete(File f) {
		if (!f.exists()) {
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
