package hide92795.novelengine.filecreator.saver;

import hide92795.novelengine.filecreator.VarNumManager;
import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import javax.crypto.CipherOutputStream;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

public class SaverStory extends Saver {

	private LinkedList<Object> commandLine;
	private StreamTokenizer tokenizer;
	private String chapterID;
	private boolean block;

	public static final int CHAPTER_BOOT = -1;
	public static final int CHAPTER_START = -2;
	public static final int CHAPTER_MENU = -3;

	private final static char END = ';';
	private final static char DOUBLE_QUOTE = '"';
	private final static char COMMA = ',';
	private final static char BLOCK_START = '{';
	private final static char BLOCK_END = '}';
	private final static char ARGUMENT_START = '(';
	private final static char ARGUMENT_END = ')';

	private static final String[] SUPPORTED_IF_OPERATOR = { "<=", "＜＝", "≦", ">=", "＞＝", "≧", ">", "＞", "<", "＜", "=",
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

	private static final String[] SUPPORTED_OPERATOR = { "+", "＋", "-", "－", "*", "×", "/", "÷", "%", "％", "&", "＆",
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
	public static final byte COMMAND_SET_CHAPTERID = 2;
	public static final byte COMMAND_SET_SCENEID = 3;
	public static final byte COMMAND_LOAD_CHAPTER = 4;
	public static final byte COMMAND_MOVE_CHAPTER = 5;
	public static final byte COMMAND_CHANGE_BG = 6;
	public static final byte COMMAND_CHANGE_CHARACTER = 7;
	public static final byte COMMAND_MOVE_CHARACTER = 8;
	public static final byte COMMAND_ACTION_CHARACTER = 9;
	public static final byte COMMAND_SHOW_CG = 10;
	public static final byte COMMAND_SHOW_WORDS = 11;
	public static final byte COMMAND_MAKE_BUTTON = 12;
	public static final byte COMMAND_IF = 13;
	public static final byte COMMAND_PLAY_BGM = 14;
	public static final byte COMMAND_STOP_BGM = 15;
	public static final byte COMMAND_PLAY_SE = 16;
	public static final byte COMMAND_SHOW_BOX = 17;
	public static final byte COMMAND_HIDE_BOX = 18;
	public static final byte COMMAND_SET_VARIABLE = 19;
	public static final byte COMMAND_SET_BACKGROUND_COLOR = 20;
	public static final byte COMMAND_EFFECT = 21;
	public static final byte COMMAND_RANDOM = 22;
	public static final byte COMMAND_CALCULATION = 23;
	public static final byte COMMAND_EXIT = 24;
	public static final byte COMMAND_WAIT = 25;

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
				// );確認
				if (tokenizer.nextToken() != ARGUMENT_END) {
					error(")", -1, "");
				}
				if (tokenizer.nextToken() != END) {
					error(";", 0, "");
				}
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
					error("ブロック", 0, "{");
				} else {
					block = true;
					commandLine.add(COMMAND_BLOCK_START);
				}
				break;
			case BLOCK_END:
				if (!block) {
					error("ブロック", 0, "}");
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
	}

	private int nextToken() throws Exception {
		int token = tokenizer.nextToken();
		if (token != COMMA) {
			error("区切り文字", -1, ",");
		}
		token = tokenizer.nextToken();
		return token;
	}

	private void parse() throws Exception {
		String command = tokenizer.sval;
		int next = tokenizer.nextToken();
		if (next != ARGUMENT_START) {
			error("", 0, "");
		}
		next = tokenizer.nextToken();
		if (command.equals("チャプター")) {
			// 文字列 チャプターID
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("チャプター", 1, "チャプターID");
			} else if (block) {
				// ブロック内
				error("チャプター", 0, "");
			} else {
				chapterID = tokenizer.sval.trim();
			}
		} else if (command.equals("シーン")) {
			// 文字列 シーンID
			commandLine.add(COMMAND_SET_SCENEID);
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("シーン", 1, "シーンID");
			} else if (block) {
				// ブロック内
				error("シーン", 0, "");
			} else {
				commandLine.add(VarNumManager.SCENE_ID.add(tokenizer.sval));
			}
		} else if (command.equals("ロード")) {
			// 文字列 チャプターID
			commandLine.add(COMMAND_LOAD_CHAPTER);
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("ロード", 1, "チャプターID");
			} else {
				commandLine.add(VarNumManager.CHAPTER_ID.add(tokenizer.sval));
			}
		} else if (command.equals("移動")) {
			// 文字列 チャプターID
			commandLine.add(COMMAND_MOVE_CHAPTER);
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("移動", 1, "チャプターID");
			} else {
				commandLine.add(VarNumManager.CHAPTER_ID.add(tokenizer.sval));
			}
		} else if (command.equals("背景変更")) {
			// 文字列 画像ID, 数値 対象, 数値 左上X座標, 数値 左上Y座標, 数値 拡大率, 数値 遅延（ms）
			commandLine.add(COMMAND_CHANGE_BG);
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("背景変更", 1, "画像ID");
			} else {
				String s = tokenizer.sval;
				int i = VarNumManager.IMAGE.add(s);
				commandLine.add(i);
			}
			next = nextToken();
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				error("背景変更", 2, "対象");
			} else {
				int i = (byte) tokenizer.nval;
				commandLine.add(i);
			}
			next = nextToken();
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				error("背景変更", 3, "左上X座標");
			} else {
				int i = (int) tokenizer.nval;
				commandLine.add(i);
			}
			next = nextToken();
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				error("背景変更", 4, "左上Y座標");
			} else {
				int i = (int) tokenizer.nval;
				commandLine.add(i);
			}
			next = nextToken();
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				error("背景変更", 5, "拡大率");
			} else {
				int i = (int) tokenizer.nval;
				commandLine.add(i);
			}
			next = nextToken();
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				error("背景変更", 6, "遅延");
			} else {
				int i = (int) tokenizer.nval;
				commandLine.add(i);
			}
		} else if (command.equals("画面効果")) {
			// 数値 対象, 数値 遅延（ms）, エフェクター エフェクト
			commandLine.add(COMMAND_EFFECT);
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非文字
				error("画面効果", 1, "対象");
			} else {
				byte i = (byte) tokenizer.nval;
				commandLine.add(i);
			}
			next = nextToken();
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				error("画面効果", 2, "遅延");
			} else {
				int i = (int) tokenizer.nval;
				commandLine.add(i);
			}
			next = nextToken();
			if (next != StreamTokenizer.TT_WORD) {
				error("画面効果", 3, "エフェクト");
			} else {
				parse();
			}
		} else if (command.equals("エフェクト")) {
			// 背景変更内フェード
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("エフェクト", 1, "エフェクターID");
			} else {
				commandLine.add(tokenizer.sval.hashCode());
			}
			while ((next = tokenizer.nextToken()) != ARGUMENT_END) {
				switch (next) {
				case StreamTokenizer.TT_EOF:
				case BLOCK_END:
				case END:
					error("エフェクト", 0, "");
					break;
				case StreamTokenizer.TT_NUMBER:
					int i = (int) tokenizer.nval;
					commandLine.add(i);
					break;
				case StreamTokenizer.TT_WORD:
					commandLine.add(Boolean.parseBoolean(tokenizer.sval));
					break;
				case DOUBLE_QUOTE:
					commandLine.add(tokenizer.sval.hashCode());
					break;
				}
			}
		} else if (command.equals("キャラ変更")) {
			// 文字列 キャラID, 数値 対象, 数値 遅延, 文字列 位置ID, 文字列 表情ID, キャラフェーダー　フェード
			commandLine.add(COMMAND_CHANGE_CHARACTER);
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("キャラ変更", 1, "キャラID");
			} else {
				commandLine.add(VarNumManager.CHARACTER.add(tokenizer.sval));
			}
			next = nextToken();
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				error("キャラ変更", 2, "対象");
			} else {
				int i = (byte) tokenizer.nval;
				commandLine.add(i);
			}
			next = nextToken();
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				error("キャラ変更", 3, "遅延");
			} else {
				int i = (int) tokenizer.nval;
				commandLine.add(i);
			}
			next = nextToken();
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("キャラ変更", 4, "位置ID");
			} else {
				commandLine.add(VarNumManager.CHARACTER_POSITION.add(tokenizer.sval));
			}
			next = nextToken();
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("キャラ変更", 5, "表情ID");
			} else {
				commandLine.add(VarNumManager.FACE_TYPE.add(tokenizer.sval));
			}
		} else if (command.equals("キャラフェーダー")) {
			// キャラ変更内フェード
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("キャラフェード", 1, "");
			} else {
				commandLine.add(tokenizer.sval.hashCode());
			}
			while ((next = tokenizer.nextToken()) != ARGUMENT_END) {
				switch (next) {
				case StreamTokenizer.TT_EOF:
				case BLOCK_END:
				case END:
					error("キャラフェード", 0, "");
					break;
				case StreamTokenizer.TT_NUMBER:
					int i = (int) tokenizer.nval;
					commandLine.add(i);
					break;
				case StreamTokenizer.TT_WORD:
					commandLine.add(Boolean.parseBoolean(tokenizer.sval));
					break;
				case DOUBLE_QUOTE:
					commandLine.add(tokenizer.sval.hashCode());
					break;
				}
			}
		} else if (command.equals("キャラ移動")) {
			// 数値 優先度, 数値 遅延, 文字列 移動前位置ID, 文字列 移動後位置ID, 数値 時間, 真偽値 加速
			commandLine.add(COMMAND_MOVE_CHARACTER);
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				error("キャラ移動", 1, "優先度");
			} else {
				int i = (int) tokenizer.nval;
				commandLine.add(i);
			}
			next = nextToken();
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				error("キャラ移動", 2, "遅延");
			} else {
				int i = (int) tokenizer.nval;
				commandLine.add(i);
			}
			next = nextToken();
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("キャラ移動", 3, "移動前位置ID");
			} else {
				commandLine.add(tokenizer.sval.hashCode());
			}
			next = nextToken();
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("キャラ移動", 4, "移動後位置ID");
			} else {
				commandLine.add(tokenizer.sval.hashCode());
			}
			next = nextToken();
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				error("キャラ移動", 5, "時間");
			} else {
				int i = (int) tokenizer.nval;
				commandLine.add(i);
			}
			next = nextToken();
			if (next != StreamTokenizer.TT_WORD) {
				// 真偽値
				error("キャラ移動", 6, "加速");
			} else {
				commandLine.add(Boolean.parseBoolean(tokenizer.sval));
			}
		} else if (command.equals("キャラアクション")) {

		} else if (command.equals("CG表示")) {
			// 文字列 CGID
			commandLine.add(COMMAND_SHOW_CG);
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("CG表示", 1, "CGID");
			} else {
				commandLine.add(tokenizer.sval.hashCode());
			}
		} else if (command.equals("セリフ")) {
			// 文字列 キャラID, 文字列 表示文字列
			commandLine.add(COMMAND_SHOW_WORDS);
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("セリフ", 1, "キャラID");
			} else {
				commandLine.add(VarNumManager.CHARACTER.add(tokenizer.sval));
			}
			// next = nextToken();
			// if (next != DOUBLE_QUOTE) {
			// // 非文字
			// error("セリフ", 2, "音声ID");
			// } else {
			// String s = "Voice_" + tokenizer.sval;
			// commandLine.add(s.hashCode());
			// }
			next = nextToken();
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("セリフ", 2, "表示文字列");
			} else {
				commandLine.add(tokenizer.sval);
			}
		} else if (command.equals("ボタン")) {
			commandLine.add(COMMAND_MAKE_BUTTON);
			int button_num = 0;
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				error("ボタン", 1, "ボタン数");
			} else {
				button_num = (int) tokenizer.nval;
				commandLine.add(button_num);
			}
			next = nextToken();
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("ボタン", 2, "配置");
			} else {
				commandLine.add(VarNumManager.BUTTON_POSITION.add(tokenizer.sval));
			}
			for (int i = 0; i < button_num; i++) {
				next = nextToken();
				if (next != DOUBLE_QUOTE) {
					// 非文字
					error("ボタン", button_num, "ボタンID");
				} else {
					commandLine.add(VarNumManager.BUTTON.add(tokenizer.sval));
				}
				next = nextToken();
				if (next != DOUBLE_QUOTE) {
					// 非文字
					error("ボタン", button_num, "移動先シーンID");
				} else {
					commandLine.add(VarNumManager.SCENE_ID.add(tokenizer.sval));
				}
			}

		} else if (command.equals("もし")) {
			// 文字列 条件, 数値 真, 数値 偽
			commandLine.add(COMMAND_IF);
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("もし", 1, "条件");
			} else {
				String condition = searchCondition(tokenizer.sval);
				String[] args = tokenizer.sval.split(condition);
				if (args.length != 2) {
					// 条件不足
					error("もし", 1, "条件式");
				}
				commandLine.add(parseCondition(condition));
				parseVariable(args[0]);
				parseVariable(args[1]);
			}
			next = nextToken();
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("もし", 2, "移動先シーンID（真）");
			} else {
				commandLine.add(VarNumManager.SCENE_ID.add(tokenizer.sval));
			}
			next = nextToken();
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("もし", 3, "移動先シーンID（偽）");
			} else {
				commandLine.add(VarNumManager.SCENE_ID.add(tokenizer.sval));
			}
		} else if (command.equals("BGM再生")) {
			// 文字列 音楽ID
			commandLine.add(COMMAND_PLAY_BGM);
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("BGM再生", 1, "音楽ID");
			} else {
				String s = tokenizer.sval;
				int i = VarNumManager.SOUND.add(s);
				commandLine.add(i);
			}
		} else if (command.equals("BGM停止")) {
			// 文字列 音楽ID
			commandLine.add(COMMAND_STOP_BGM);
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("BGM停止", 1, "音楽ID");
			} else {
				String id = "m_" + tokenizer.sval;
				commandLine.add(id.hashCode());
			}
		} else if (command.equals("SE再生")) {
			// 文字列 効果音ID
			commandLine.add(COMMAND_PLAY_SE);
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("SE再生", 1, "効果音ID");
			} else {
				String s = tokenizer.sval;
				int i = VarNumManager.SOUND.add(s);
				commandLine.add(i);
			}
		} else if (command.equals("ボックス表示")) {
			commandLine.add(COMMAND_SHOW_BOX);
		} else if (command.equals("ボックス閉じる")) {
			commandLine.add(COMMAND_HIDE_BOX);
		} else if (command.equals("設定")) {
			// 文字列 変数名, 数値 設定値
			commandLine.add(COMMAND_SET_VARIABLE);
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("設定", 1, "変数名");
			} else {
				String[] var = tokenizer.sval.split("\\.");
				int varType = parseVariableType(var[0]);
				commandLine.add(varType);
				commandLine.add(var[1]);
			}
			next = nextToken();
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				error("設定", 2, "設定値");
			} else {
				int i = (int) tokenizer.nval;
				commandLine.add(i);
			}
		} else if (command.equals("背景色")) {
			// 数値 対象, 文字列 色(HTML/定義済み/3数字), 数値 アルファ値
			commandLine.add(COMMAND_SET_BACKGROUND_COLOR);
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				error("背景色", 1, "対象");
			} else {
				int i = (int) tokenizer.nval;
				commandLine.add(i);
			}
			next = nextToken();
			Color color = null;
			if (next == DOUBLE_QUOTE) {
				// 文字列表現
				String c = tokenizer.sval;
				if (c.startsWith("#")) {
					// HTML表記
					color = Color.decode(c);
				} else {
					// 定義済み
					Class<Color> c_class = Color.class;
					Field f = c_class.getField(c);
					color = (Color) f.get(null);
				}
			} else if (next == StreamTokenizer.TT_NUMBER) {
				// 3数字RGB表現
				int r = (int) tokenizer.nval;
				next = nextToken();
				if (next != StreamTokenizer.TT_NUMBER) {
					// 非数値
					error("背景色", 2, "G");
				}
				int g = (int) tokenizer.nval;

				next = nextToken();
				if (next != StreamTokenizer.TT_NUMBER) {
					// 非数値
					error("背景色", 3, "B");
				}
				int b = (int) tokenizer.nval;
				color = new Color(r, g, b);
			} else {
				error("背景色", 1, "文字列表現/R");
			}
			commandLine.add(color.getRGB());
			next = nextToken();
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				error("背景透明度", 2, "アルファ値");
			} else {
				int i = (int) tokenizer.nval;
				commandLine.add(i);

			}
		} else if (command.equals("乱数")) {
			// 文字列 変数名, 数値 範囲
			commandLine.add(COMMAND_RANDOM);
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("乱数", 1, "変数名");
			} else {
				String[] var = tokenizer.sval.split("\\.");
				byte varType = parseVariableType(var[0]);
				commandLine.add(varType);
				commandLine.add(Integer.parseInt(var[1]));
			}
			next = nextToken();
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				error("乱数", 2, "個数");
			} else {
				int i = (int) tokenizer.nval;
				commandLine.add(i);
			}
		} else if (command.equals("計算")) {
			// 文字列 変数名, 文字列 計算式
			commandLine.add(COMMAND_CALCULATION);
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("計算", 1, "変数名");
			} else {
				String[] var = tokenizer.sval.split("\\.");
				int varType = parseVariableType(var[0]);
				commandLine.add(varType);
				commandLine.add(Integer.parseInt(var[1]));
			}
			next = nextToken();
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("計算", 1, "計算式");
			} else {
				String operation = searchOperation(tokenizer.sval);
				String[] args = tokenizer.sval.split(operation);
				if (args.length != 2) {
					// 条件不足
					error("計算", 1, "計算式");
				}
				commandLine.add(parseOperation(operation));
				parseVariable(args[0]);
				parseVariable(args[1]);
			}
		} else if (command.equals("終了")) {
			// 真偽値 確認
			commandLine.add(COMMAND_EXIT);
			if (next != StreamTokenizer.TT_WORD) {
				// 真偽値
				error("終了", 1, "確認");
			} else {
				commandLine.add(Boolean.parseBoolean(tokenizer.sval));
			}
		} else if (command.equals("待機")) {
			// 数値 待機時間, 文字列 単位
			commandLine.add(COMMAND_WAIT);
			int time = 0;
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				error("待機", 1, "待機時間");
			} else {
				time = (int) tokenizer.nval;
			}
			next = nextToken();
			if (next != DOUBLE_QUOTE) {
				// 非文字
				error("待機", 2, "単位");
			} else {
				String prefix = tokenizer.sval;
				if (prefix.toLowerCase().equals("s")) {
					// 秒
					commandLine.add(time * 1000);
				} else if (prefix.toLowerCase().equals("ms")) {
					// ミリ秒
					commandLine.add(time);
				} else {
					error("待機", 2, "単位");
				}
			}
		}
	}

	private byte parseVariableType(String var) {
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

	private void parseVariable(String variable) {
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


	private void error(String comName, int argNum, String string2) throws Exception {
		System.err.println("---構文エラー---");
		System.err.println("行番号:" + tokenizer.lineno());
		System.err.println("コマンド名:" + comName);
		System.err.println("引数:" + string2 + " (" + argNum + ")");
		System.err.println();
		throw new Exception();
	}

	private String searchCondition(String s) {
		for (String condition : SUPPORTED_IF_OPERATOR) {
			if (s.contains(condition)) {
				return condition;
			}
		}
		return null;
	}

	private byte parseCondition(String condition) {
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

	private String searchOperation(String s) {
		for (String operation : SUPPORTED_OPERATOR) {
			if (s.contains(operation)) {
				return operation;
			}
		}
		return null;
	}

	private int parseOperation(String operation) {
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
