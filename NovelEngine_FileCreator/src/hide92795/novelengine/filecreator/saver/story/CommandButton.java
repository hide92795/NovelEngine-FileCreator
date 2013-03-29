package hide92795.novelengine.filecreator.saver.story;

import hide92795.novelengine.filecreator.CommandException;
import hide92795.novelengine.filecreator.VarNumManager;
import hide92795.novelengine.filecreator.saver.SaverStory;
import java.io.StreamTokenizer;
import java.util.LinkedList;

public class CommandButton extends Command {
	@Override
	public void save(StreamTokenizer tokenizer, LinkedList<Object> commandLine) throws Exception {
		// int 個数, String 位置ID, [String ボタンID, String 移動先シーンID]
		commandLine.add(SaverStory.COMMAND_MAKE_BUTTON);
		int next = tokenizer.nextToken();
		if (next != StreamTokenizer.TT_NUMBER) {
			// 非数値
			throw new CommandException(tokenizer.lineno(), "ボタン", 1, "引数「個数」は数値でなければいけません。");
		} else {
			commandLine.add((int) tokenizer.nval);
		}
		next = nextArgument(tokenizer);
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "ボタン", 2, "引数「位置ID」は文字列でなければいけません。");
		} else {
			commandLine.add(VarNumManager.BUTTON_POSITION.add(tokenizer.sval));
		}
		int argNum = 2;
		next = nextArgument(tokenizer);
		for (;;) {
			argNum++;
			if (next != SaverStory.DOUBLE_QUOTE) {
				// 非文字
				throw new CommandException(tokenizer.lineno(), "ボタン", argNum, "引数「ボタンID」は文字列でなければいけません。");
			} else {
				commandLine.add(VarNumManager.BUTTON.add(tokenizer.sval));
			}
			argNum++;
			next = nextArgument(tokenizer);
			if (next != SaverStory.DOUBLE_QUOTE) {
				// 非文字
				throw new CommandException(tokenizer.lineno(), "ボタン", argNum, "引数「移動先シーンID」は文字列でなければいけません。");
			} else {
				commandLine.add(VarNumManager.SCENE_ID.add(tokenizer.sval));
			}
			int token = tokenizer.nextToken();
			if (token == SaverStory.COMMA) {
				next = tokenizer.nextToken();
			} else if (token == SaverStory.ARGUMENT_END) {
				break;
			} else {
				throw new CommandException(tokenizer.lineno(), "区切り文字", -1, "引数が不足している、もしくは区切り文字が不正です。");
			}
		}
	}
}
