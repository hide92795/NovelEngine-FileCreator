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
package hide92795.novelengine.filecreator.saver;

import hide92795.novelengine.filecreator.CommandException;
import hide92795.novelengine.filecreator.VarNumManager;
import hide92795.novelengine.filecreator.saver.story.Command;
import hide92795.novelengine.filecreator.saver.story.CommandAssignment;
import hide92795.novelengine.filecreator.saver.story.CommandBackGroundEffect;
import hide92795.novelengine.filecreator.saver.story.CommandButton;
import hide92795.novelengine.filecreator.saver.story.CommandCalculation;
import hide92795.novelengine.filecreator.saver.story.CommandChangeBackGround;
import hide92795.novelengine.filecreator.saver.story.CommandChangeBackGroundColor;
import hide92795.novelengine.filecreator.saver.story.CommandChangeBackGroundFigure;
import hide92795.novelengine.filecreator.saver.story.CommandChangeCharacter;
import hide92795.novelengine.filecreator.saver.story.CommandExit;
import hide92795.novelengine.filecreator.saver.story.CommandHideBox;
import hide92795.novelengine.filecreator.saver.story.CommandIF;
import hide92795.novelengine.filecreator.saver.story.CommandLoadChapter;
import hide92795.novelengine.filecreator.saver.story.CommandMoveChapter;
import hide92795.novelengine.filecreator.saver.story.CommandPlayBGM;
import hide92795.novelengine.filecreator.saver.story.CommandPlaySE;
import hide92795.novelengine.filecreator.saver.story.CommandRandom;
import hide92795.novelengine.filecreator.saver.story.CommandShowBox;
import hide92795.novelengine.filecreator.saver.story.CommandShowWords;
import hide92795.novelengine.filecreator.saver.story.CommandStopBGM;
import hide92795.novelengine.filecreator.saver.story.CommandSystemLoad;
import hide92795.novelengine.filecreator.saver.story.CommandSystemSave;
import hide92795.novelengine.filecreator.saver.story.CommandVoice;
import hide92795.novelengine.filecreator.saver.story.CommandWait;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import javax.crypto.CipherOutputStream;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

/**
 * ストーリーデータを保存するセーバーです。
 * 
 * @author hide92795
 */
public class SaverStory extends Saver {
	/**
	 * 各コマンドのセーバーを保存するマップです。
	 */
	private static HashMap<String, Command> commands;

	static {
		commands = new HashMap<String, Command>();
		commands.put("ロード", new CommandLoadChapter());
		commands.put("移動", new CommandMoveChapter());
		commands.put("背景変更", new CommandChangeBackGround());
		commands.put("キャラ変更", new CommandChangeCharacter());
		commands.put("ボイス", new CommandVoice());
		// 8 COMMAND_EFFECT_CHARACTER
		commands.put("背景範囲", new CommandChangeBackGroundFigure());
		commands.put("セリフ", new CommandShowWords());
		commands.put("ボタン", new CommandButton());
		commands.put("もし", new CommandIF());
		commands.put("BGM再生", new CommandPlayBGM());
		commands.put("BGM停止", new CommandStopBGM());
		commands.put("SE再生", new CommandPlaySE());
		commands.put("ボックス表示", new CommandShowBox());
		commands.put("ボックス閉じる", new CommandHideBox());
		commands.put("設定", new CommandAssignment());
		commands.put("背景色", new CommandChangeBackGroundColor());
		commands.put("背景エフェクト", new CommandBackGroundEffect());
		commands.put("乱数", new CommandRandom());
		commands.put("計算", new CommandCalculation());
		commands.put("終了", new CommandExit());
		commands.put("待機", new CommandWait());
		commands.put("システムセーブ", new CommandSystemSave());
		commands.put("システムロード", new CommandSystemLoad());
	}
	/**
	 * 解析したストーリーデータを保存するリストです。
	 */
	private LinkedList<Object> commandLine;
	/**
	 * ソースデータを解析する {@link java.io.StreamTokenizer StreamTokenizer} です。
	 */
	private StreamTokenizer tokenizer;
	/**
	 * このストーリーのチャプターIDです。
	 */
	private String chapterID;
	/**
	 * 現在解析中のラインがブロック内部かどうかを表します。
	 */
	private boolean block;

	/**
	 * 起動時に各種リソースを読み込むためのストーリーデータのIDを示します。
	 */
	public static final int CHAPTER_BOOT = -1;
	/**
	 * 一番最初に読み込まれるストーリーデータのIDを示します。
	 */
	public static final int CHAPTER_START = -2;
	/**
	 * メニューとして読み込まれるストーリーデータのIDを示します。
	 */
	public static final int CHAPTER_MENU = -3;

	/**
	 * コマンドの終了文字を表します。
	 */
	public static final char END = ';';
	/**
	 * 文字列トークンを表します。
	 */
	public static final char DOUBLE_QUOTE = '"';
	/**
	 * 区切り文字を表します。
	 */
	public static final char COMMA = ',';
	/**
	 * ブロック開始文字を表します。
	 */
	public static final char BLOCK_START = '{';
	/**
	 * ブロック終了文字を表します。
	 */
	public static final char BLOCK_END = '}';
	/**
	 * 引数開始文字を表します。
	 */
	public static final char ARGUMENT_START = '(';
	/**
	 * 引数終了文字を表します。
	 */
	public static final char ARGUMENT_END = ')';

	/**
	 * 「もし」コマンドでサポートしている比較演算子です。
	 */
	public static final String[] SUPPORTED_IF_OPERATOR = { "<=", "＜＝", "≦", ">=", "＞＝", "≧", ">", "＞", "<", "＜", "=",
			"＝" };
	/**
	 * イコールを表します。
	 */
	public static final int IF_EQUAL = 0;
	/**
	 * 大なりを表します。
	 */
	public static final int IF_GREATER = 1;
	/**
	 * 小なりを表します。
	 */
	public static final int IF_LESS = 2;
	/**
	 * 大なりイコールを表します。
	 */
	public static final int IF_GREATER_EQUAL = 3;
	/**
	 * 小なりイコールを表します。
	 */
	public static final int IF_LESS_EQUAL = 4;

	/**
	 * 変数ではなく生の値を表します。変数名がそのまま値となります。
	 */
	public static final byte VARIABLE_RAW = -1;
	/**
	 * グローバル変数を表す定数です。
	 */
	public static final byte VARIABLE_GLOBAL = 0;
	/**
	 * プライベート変数を表す定数です。
	 */
	public static final byte VARIABLE_PRIVATE = 1;
	/**
	 * 一時変数を表す定数です。
	 */
	public static final byte VARIABLE_TEMP = 2;
	/**
	 * システム変数を表す定数です。
	 */
	public static final byte VARIABLE_SYSTEM = 3;
	/**
	 * ゲームの内部的な設定情報を表す定数です。
	 */
	public static final byte VARIABLE_SETTING = 4;
	/**
	 * ゲームの描画に関する設定情報を表す定数です。
	 */
	public static final byte VARIABLE_RENDER = 5;

	/**
	 * 「計算」コマンドでサポートしている演算子です。
	 */
	public static final String[] SUPPORTED_OPERATOR = { "+", "＋", "-", "－", "*", "×", "/", "÷", "%", "％", "&", "＆",
			"|", "｜", "^", "＾", "<<", "＜＜", ">>", "＞＞", ">>>", "＞＞＞" };
	/**
	 * 加算を表す定数です。
	 */
	public static final byte OPERATOR_PLUS = 0;
	/**
	 * 減算を表す定数です。
	 */
	public static final byte OPERATOR_MINUS = 1;
	/**
	 * 乗算を表す定数です。
	 */
	public static final byte OPERATOR_TIMES = 2;
	/**
	 * 除算を表す定数です。
	 */
	public static final byte OPERATOR_DIVIDED = 3;
	/**
	 * モジュラ計算を表す定数です。
	 */
	public static final byte OPERATOR_MODULO = 4;
	/**
	 * 論理積演算を表す定数です。
	 */
	public static final byte OPERATOR_AND = 5;
	/**
	 * 論理和演算を表す定数です。
	 */
	public static final byte OPERATOR_OR = 6;
	/**
	 * 排他的論理和演算を表す定数です。
	 */
	public static final byte OPERATOR_XOR = 7;
	/**
	 * 左算術シフト演算を表す定数です。
	 */
	public static final byte OPERATOR_SHIFT_LEFT = 8;
	/**
	 * 右算術シフト演算を表す定数です。
	 */
	public static final byte OPERATOR_SHIFT_RIGHT = 9;
	/**
	 * 右論理シフト演算を表す定数です。
	 */
	public static final byte OPERATOR_SHIFT_RIGHT_UNSIGNED = 10;

	/**
	 * ブロックの開始地点を表します。
	 */
	public static final byte COMMAND_BLOCK_START = 0;
	/**
	 * ブロックの終了地点を表します。
	 */
	public static final byte COMMAND_BLOCK_END = 1;
	/**
	 * シーンIDを設定するストーリーデータを表します。
	 */
	public static final byte COMMAND_SET_SCENEID = 2;
	/**
	 * チャプターをロードするストーリーデータを表します。
	 * 
	 * @see CommandLoadChapter
	 */
	public static final byte COMMAND_LOAD_CHAPTER = 3;
	/**
	 * チャプターを移動するストーリーデータを表します。
	 * 
	 * @see CommandMoveChapter
	 */
	public static final byte COMMAND_MOVE_CHAPTER = 4;
	/**
	 * 背景を変更するストーリーデータを表します。
	 * 
	 * @see CommandChangeBackGround
	 */
	public static final byte COMMAND_SET_BACKGROUND = 5;
	/**
	 * キャラクターを変更するストーリーデータを表します。
	 * 
	 * @see CommandChangeCharacter
	 */
	public static final byte COMMAND_SET_CHARACTER = 6;
	/**
	 * ボイスの再生を行うストーリーデータを表します。
	 * 
	 * @see CommandVoice
	 */
	public static final byte COMMAND_VOICE = 7;
	/**
	 * 背景エフェクトを実行するストーリーデータを表します。
	 * 
	 * @see CommandBackGroundEffect
	 */
	public static final byte COMMAND_EFFECT_CHARACTER = 8;
	/**
	 * 背景の範囲を変更するストーリーデータを表します。
	 * 
	 * @see CommandChangeBackGroundFigure
	 */
	public static final byte COMMAND_SET_BACKGROUND_FIGURE = 9;
	/**
	 * 文章を表示するストーリーデータを表します。
	 * 
	 * @see CommandShowWords
	 */
	public static final byte COMMAND_SHOW_WORDS = 10;
	/**
	 * ボタンを表示するストーリーデータを表します。
	 * 
	 * @see CommandButton
	 */
	public static final byte COMMAND_MAKE_BUTTON = 11;
	/**
	 * 条件分岐を行うストーリーデータを表します。
	 * 
	 * @see CommandIF
	 */
	public static final byte COMMAND_IF = 12;
	/**
	 * BGMを再生するストーリーデータを表します。
	 * 
	 * @see CommandPlayBGM
	 */
	public static final byte COMMAND_PLAY_BGM = 13;
	/**
	 * BGMを停止するストーリーデータを表します。
	 * 
	 * @see CommandStopBGM
	 */
	public static final byte COMMAND_STOP_BGM = 14;
	/**
	 * SEを再生するストーリーデータを表します。
	 * 
	 * @see CommandPlaySE
	 */
	public static final byte COMMAND_PLAY_SE = 15;
	/**
	 * ボックスを表示するストーリーデータを表します。
	 * 
	 * @see CommandShowBox
	 */
	public static final byte COMMAND_SHOW_BOX = 16;
	/**
	 * ボックスを隠すストーリーデータを表します。
	 * 
	 * @see CommandShowBox
	 */
	public static final byte COMMAND_HIDE_BOX = 17;
	/**
	 * 変数を設定するストーリーデータを表します。
	 * 
	 * @see CommandAssignment
	 */
	public static final byte COMMAND_SET_VARIABLE = 18;
	/**
	 * 背景色を変更するストーリーデータを表します。
	 * 
	 * @see CommandChangeBackGroundColor
	 */
	public static final byte COMMAND_SET_BACKGROUND_COLOR = 19;
	/**
	 * 背景エフェクトを実行するストーリーデータを表します。
	 * 
	 * @see CommandBackGroundEffect
	 */
	public static final byte COMMAND_EFFECT_BACKGROUND = 20;
	/**
	 * 乱数を発生させるストーリーデータを表します。
	 * 
	 * @see CommandRandom
	 */
	public static final byte COMMAND_RANDOM = 21;
	/**
	 * 計算を行うストーリーデータを表します。
	 * 
	 * @see CommandCalculation
	 */
	public static final byte COMMAND_CALCULATION = 22;
	/**
	 * ゲームを終了するストーリーデータを表します。
	 * 
	 * @see CommandExit
	 */
	public static final byte COMMAND_EXIT = 23;
	/**
	 * 指定時間待機するストーリーデータを表します。
	 * 
	 * @see CommandWait
	 */
	public static final byte COMMAND_WAIT = 24;
	/**
	 * 現在の状態を外部ファイルに保存し、再開できるようにするストーリーデータを表します。
	 * 
	 * @see CommandSystemSave
	 */
	public static final byte COMMAND_SYSTEM_LOAD = 25;
	/**
	 * 外部ファイルからセーブデータを読み込み、ゲームを再開するストーリーデータを表します。
	 * 
	 * @see CommandSystemLoad
	 */
	public static final byte COMMAND_SYSTEM_SAVE = 26;

	/**
	 * ストーリーデータを保存するセーバーを生成します。
	 * 
	 * @param outputDir
	 *            出力先のディレクトリ
	 * @param src
	 *            必要なファイルが保管されているディレクトリ、もしくはファイル
	 * @param crypt
	 *            暗号化に関する情報を保存するプロパティ
	 * @param encoding
	 *            読み込み時に使用する文字コード
	 */
	public SaverStory(File outputDir, File src, Properties crypt, String encoding) {
		super(outputDir, src, crypt, encoding);
		commandLine = new LinkedList<Object>();

	}

	@Override
	public void pack() throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(getSrc()), getEncoding()));
		tokenizer = new StreamTokenizer(reader);
		tokenizer.resetSyntax();

		tokenizer.wordChars('0', '9');
		tokenizer.wordChars('a', 'z');
		tokenizer.wordChars('A', 'Z');
		tokenizer.wordChars('_', '_');
		tokenizer.whitespaceChars(' ', ' ');
		tokenizer.whitespaceChars('\t', '\t');
		tokenizer.whitespaceChars('\n', '\n');
		tokenizer.whitespaceChars('\r', '\r');
		tokenizer.quoteChar(DOUBLE_QUOTE);
		tokenizer.parseNumbers();
		tokenizer.eolIsSignificant(false);
		tokenizer.slashStarComments(true);
		tokenizer.slashSlashComments(true);

		int token;
		while ((token = tokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
			switch (token) {
			case StreamTokenizer.TT_EOL:
				break;
			case StreamTokenizer.TT_NUMBER:
				System.out.println("" + tokenizer.nval + " <Num>");
				break;
			case StreamTokenizer.TT_WORD:
				parse();
				break;
			case DOUBLE_QUOTE:
				break;
			case END:
				System.out.println("<Command End>");
				break;
			case COMMA:
				break;
			case BLOCK_START:
				if (block) {
					throw new CommandException(tokenizer.lineno(), "ブロック", -1, "ブロック内にブロックは設置出来ません。");
				} else {
					block = true;
					commandLine.add(COMMAND_BLOCK_START);
				}
				break;
			case BLOCK_END:
				if (!block) {
					throw new CommandException(tokenizer.lineno(), "ブロック", -1, "ブロックが開始していません。");
				} else {
					block = false;
					commandLine.add(COMMAND_BLOCK_END);
				}
				break;
			default:
				break;
			}
		}
		String filename;
		if (chapterID.equals("start")) {
			filename = "start.nes";
		} else if (chapterID.equals("menu")) {
			filename = "menu.nes";
		} else {
			int chapterId_i = VarNumManager.CHAPTER_ID.add(chapterID);
			filename = chapterId_i + ".nes";
		}

		CipherOutputStream cos = createCipherInputStream(new File(getOutputDir(), filename), getCryptProperties());

		MessagePack msgpack = new MessagePack();
		Packer p = msgpack.createPacker(cos);
		Iterator<Object> i = commandLine.iterator();
		while (i.hasNext()) {
			Object object = i.next();
			p.write(object);
		}

		p.flush();
		p.close();
		reader.close();
	}

	/**
	 * コマンドを解析します。
	 * 
	 * @throws Exception
	 *             何らかのエラーが発生した場合
	 */
	private void parse() throws Exception {
		String command_s = tokenizer.sval;
		int next = tokenizer.nextToken();
		if (next != ARGUMENT_START) {
			throw new CommandException(tokenizer.lineno(), command_s, -1, "変数の記述が不正です。");
		}
		if (command_s.equals("チャプター")) {
			// String チャプターID
			next = tokenizer.nextToken();
			if (next != DOUBLE_QUOTE) {
				// 非文字
				throw new CommandException(tokenizer.lineno(), "チャプター", 1, "引数「チャプターID」は文字列でなければいけません。");
			} else if (block) {
				// ブロック内
				throw new CommandException(tokenizer.lineno(), "チャプター", -1, "「チャプター」コマンドはブロック内に置けません。");
			} else {
				chapterID = tokenizer.sval.trim();
			}
			next = tokenizer.nextToken();
			if (next != SaverStory.ARGUMENT_END) {
				throw new CommandException(tokenizer.lineno(), "チャプター", -1, "引数が閉じられていません。");
			}
		} else if (command_s.equals("シーン")) {
			// String シーンID
			commandLine.add(COMMAND_SET_SCENEID);
			next = tokenizer.nextToken();
			if (next != DOUBLE_QUOTE) {
				// 非文字
				throw new CommandException(tokenizer.lineno(), "シーン", 1, "引数「シーンID」は文字列でなければいけません。");
			} else if (block) {
				// ブロック内
				throw new CommandException(tokenizer.lineno(), "シーン", -1, "「シーン」コマンドはブロック内に置けません。");
			} else {
				commandLine.add(VarNumManager.SCENE_ID.add(tokenizer.sval));
			}
			next = tokenizer.nextToken();
			if (next != SaverStory.ARGUMENT_END) {
				throw new CommandException(tokenizer.lineno(), "シーン", -1, "引数が閉じられていません。");
			}
		} else {
			Command command = commands.get(command_s);
			if (command == null) {
				throw new CommandException(tokenizer.lineno(), command_s, -1, "コマンド「" + command_s + "」は定義されていません。");
			}
			command.save(tokenizer, commandLine);
		}
		// ;確認
		if (tokenizer.nextToken() != SaverStory.END) {
			throw new CommandException(tokenizer.lineno(), command_s, -1, "コマンドが終了されていません。");
		}
	}

	/**
	 * 変数のタイプを判別します。
	 * 
	 * @param var
	 *            変数のタイプを表す文字列
	 * @return 変数のタイプを表すID
	 */
	public static byte parseVariableType(String var) {
		if (var.equals("global")) {
			return VARIABLE_GLOBAL;
		} else if (var.equals("private")) {
			return VARIABLE_PRIVATE;
		} else if (var.equals("temp")) {
			return VARIABLE_TEMP;
		} else if (var.equals("system")) {
			return VARIABLE_SYSTEM;
		} else if (var.equals("setting")) {
			return VARIABLE_SETTING;
		} else if (var.equals("render")) {
			return VARIABLE_RENDER;
		}
		return VARIABLE_TEMP;
	}

	/**
	 * 変数を解析し、コマンドラインに追加します。
	 * 
	 * @param variable
	 *            追加する変数
	 * @param commandLine
	 *            追加先のコマンドライン
	 */
	public static void parseVariable(String variable, LinkedList<Object> commandLine) {
		String[] s = variable.trim().split("\\.");
		if (s.length == 2) {
			// 変数
			int varType = parseVariableType(s[0].trim());
			commandLine.add(varType);
			commandLine.add(s[1].trim());
		} else {
			// 定数
			commandLine.add(VARIABLE_RAW);
			commandLine.add(variable.trim());
		}
	}

	/**
	 * 条件式内にある比較演算子を検索し、取得します。
	 * 
	 * @param s
	 *            条件式
	 * @return 比較演算子
	 */
	public static String searchCondition(String s) {
		for (String condition : SUPPORTED_IF_OPERATOR) {
			if (s.contains(condition)) {
				return condition;
			}
		}
		return null;
	}

	/**
	 * 比較演算子のタイプを判別します。
	 * 
	 * @param condition
	 *            比較演算子を表す文字列
	 * @return 比較演算子のID
	 */
	public static byte parseCondition(String condition) {
		if (condition.equals("=") || condition.equals("＝")) {
			return IF_EQUAL;
		} else if (condition.equals("<=") || condition.equals("＜＝") || condition.equals("≦")) {
			return IF_LESS_EQUAL;
		} else if (condition.equals(">=") || condition.equals("＞＝") || condition.equals("≧")) {
			return IF_GREATER_EQUAL;
		} else if (condition.equals(">") || condition.equals("＜")) {
			return IF_LESS;
		} else if (condition.equals("<") || condition.equals("＞")) {
			return IF_GREATER;
		} else {
			return IF_EQUAL;
		}
	}

	/**
	 * 計算式内にある演算子を検索し、取得します。
	 * 
	 * @param s
	 *            計算式
	 * @return 演算子
	 */
	public static String searchOperation(String s) {
		for (String operation : SUPPORTED_OPERATOR) {
			if (s.contains(operation)) {
				return operation;
			}
		}
		return null;
	}

	/**
	 * 演算子のタイプを判別します。
	 * 
	 * @param operation
	 *            演算子を表す文字列
	 * @return 比較演算子のID
	 */
	public static int parseOperation(String operation) {
		if (operation.equals("+") || operation.equals("＋")) {
			return OPERATOR_PLUS;
		} else if (operation.equals("-") || operation.equals("－")) {
			return OPERATOR_MINUS;
		} else if (operation.equals("*") || operation.equals("×")) {
			return OPERATOR_TIMES;
		} else if (operation.equals("/") || operation.equals("÷")) {
			return OPERATOR_DIVIDED;
		} else if (operation.equals("%") || operation.equals("％")) {
			return OPERATOR_MODULO;
		} else if (operation.equals("&") || operation.equals("＆")) {
			return OPERATOR_AND;
		} else if (operation.equals("|") || operation.equals("｜")) {
			return OPERATOR_OR;
		} else if (operation.equals("^") || operation.equals("＾")) {
			return OPERATOR_XOR;
		} else if (operation.equals("<<") || operation.equals("＜＜")) {
			return OPERATOR_SHIFT_LEFT;
		} else if (operation.equals(">>") || operation.equals("＞＞")) {
			return OPERATOR_SHIFT_RIGHT;
		} else if (operation.equals(">>>") || operation.equals("＞＞＞")) {
			return OPERATOR_SHIFT_RIGHT_UNSIGNED;
		}
		return OPERATOR_PLUS;
	}
}
