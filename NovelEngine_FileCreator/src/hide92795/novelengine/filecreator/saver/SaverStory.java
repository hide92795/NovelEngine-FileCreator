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
import hide92795.novelengine.filecreator.saver.story.CommandVoice;
import hide92795.novelengine.filecreator.saver.story.CommandWait;
import java.io.File;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import javax.crypto.CipherOutputStream;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

public class SaverStory extends Saver {
	private static HashMap<String, Command> commands;

	static {
		commands = new HashMap<String, Command>();
		commands.put("ロード", new CommandLoadChapter());
		commands.put("移動", new CommandMoveChapter());
		commands.put("背景変更", new CommandChangeBackGround());
		commands.put("キャラ変更", new CommandChangeCharacter());
		commands.put("ボイス", new CommandVoice());
		// 8
		// 9
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
	}
	private LinkedList<Object> commandLine;
	private StreamTokenizer tokenizer;
	private String chapterID;
	private boolean block;

	public static final int CHAPTER_BOOT = -1;
	public static final int CHAPTER_START = -2;
	public static final int CHAPTER_MENU = -3;

	public final static char END = ';';
	public final static char DOUBLE_QUOTE = '"';
	public final static char COMMA = ',';
	public final static char BLOCK_START = '{';
	public final static char BLOCK_END = '}';
	public final static char ARGUMENT_START = '(';
	public final static char ARGUMENT_END = ')';

	public static final String[] SUPPORTED_IF_OPERATOR = { "<=", "＜＝", "≦", ">=", "＞＝", "≧", ">", "＞", "<", "＜", "=",
			"＝" };
	public static final byte IF_EQUAL = 0;
	public static final byte IF_GREATER = 1;
	public static final byte IF_LESS = 2;
	public static final byte IF_GREATER_EQUAL = 3;
	public static final byte IF_LESS_EQUAL = 4;

	public static final byte VARIABLE_RAW = -1;
	public static final byte VARIABLE_GLOBAL = 0;
	public static final byte VARIABLE_PRIVATE = 1;
	public static final byte VARIABLE_TEMP = 2;
	public static final byte VARIABLE_SYSTEM = 3;
	public static final byte VARIABLE_SETTING = 4;
	public static final byte VARIABLE_RENDER = 5;

	public static final String[] SUPPORTED_OPERATOR = { "+", "＋", "-", "－", "*", "×", "/", "÷", "%", "％", "&", "＆",
			"|", "｜", "^", "＾", "<<", "＜＜", ">>", "＞＞", ">>>", "＞＞＞" };
	public static final byte OPERATOR_PLUS = 0;
	public static final byte OPERATOR_MINUS = 1;
	public static final byte OPERATOR_TIMES = 2;
	public static final byte OPERATOR_DIVIDED = 3;
	public static final byte OPERATOR_MODULO = 4;
	public static final byte OPERATOR_AND = 5;
	public static final byte OPERATOR_OR = 6;
	public static final byte OPERATOR_XOR = 7;
	public static final byte OPERATOR_SHIFT_LEFT = 8;
	public static final byte OPERATOR_SHIFT_RIGHT = 9;
	public static final byte OPERATOR_SHIFT_RIGHT_UNSIGNED = 10;

	public static final byte COMMAND_BLOCK_START = 0;
	public static final byte COMMAND_BLOCK_END = 1;
	public static final byte COMMAND_SET_SCENEID = 2;
	public static final byte COMMAND_LOAD_CHAPTER = 3;
	public static final byte COMMAND_MOVE_CHAPTER = 4;
	public static final byte COMMAND_CHANGE_BG = 5;
	public static final byte COMMAND_CHANGE_CHARACTER = 6;
	public static final byte COMMAND_VOICE = 7;
	public static final byte COMMAND_EFFECT_CHARACTER = 8;
	public static final byte COMMAND_SHOW_CG = 9;
	public static final byte COMMAND_SHOW_WORDS = 10;
	public static final byte COMMAND_MAKE_BUTTON = 11;
	public static final byte COMMAND_IF = 12;
	public static final byte COMMAND_PLAY_BGM = 13;
	public static final byte COMMAND_STOP_BGM = 14;
	public static final byte COMMAND_PLAY_SE = 15;
	public static final byte COMMAND_SHOW_BOX = 16;
	public static final byte COMMAND_HIDE_BOX = 17;
	public static final byte COMMAND_SET_VARIABLE = 18;
	public static final byte COMMAND_SET_BACKGROUND_COLOR = 19;
	public static final byte COMMAND_EFFECT_BACKGROUND = 20;
	public static final byte COMMAND_RANDOM = 21;
	public static final byte COMMAND_CALCULATION = 22;
	public static final byte COMMAND_EXIT = 23;
	public static final byte COMMAND_WAIT = 24;

	private final File src;

	public SaverStory(File src, File output, Properties crypt) {
		super(output, crypt);
		this.src = src;
		commandLine = new LinkedList<Object>();

	}

	@Override
	public void pack() throws Exception {
		FileReader reader = new FileReader(src);
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

		CipherOutputStream cos = createCipherInputStream(new File(output, filename), crypt);

		// System.out.print("IV: ");
		// byte[] iv = cipher.getIV();
		// for (int i = 0; i < iv.length; i++) {
		// System.out.print(Integer.toHexString(iv[i] & 0xff) + " ");
		// }
		// System.out.println();

		MessagePack msgpack = new MessagePack();
		Packer p = msgpack.createPacker(cos);
		Iterator<Object> i = commandLine.iterator();
		while (i.hasNext()) {
			Object object = i.next();
			// System.out.println(object);
			p.write(object);
		}

		p.flush();
		p.close();
		reader.close();
	}

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

	public static String searchCondition(String s) {
		for (String condition : SUPPORTED_IF_OPERATOR) {
			if (s.contains(condition)) {
				return condition;
			}
		}
		return null;
	}

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

	public static String searchOperation(String s) {
		for (String operation : SUPPORTED_OPERATOR) {
			if (s.contains(operation)) {
				return operation;
			}
		}
		return null;
	}

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
