package hide92795.novelengine.filecreator.saver.story;

import hide92795.novelengine.filecreator.CommandException;
import hide92795.novelengine.filecreator.VarNumManager;
import hide92795.novelengine.filecreator.saver.SaverStory;
import java.io.StreamTokenizer;
import java.util.LinkedList;

public class CommandShowWords extends Command {
	@Override
	public void save(StreamTokenizer tokenizer, LinkedList<Object> commandLine) throws Exception {
		// String キャラID, String セリフ
		commandLine.add(SaverStory.COMMAND_SHOW_WORDS);
		int next = tokenizer.nextToken();
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "セリフ", 1, "引数「キャラID」は文字列でなければいけません。");
		} else {
			commandLine.add(VarNumManager.CHARACTER.add(tokenizer.sval));
		}
		next = nextArgument(tokenizer);
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "セリフ", 2, "引数「セリフ」は文字列でなければいけません。");
		} else {
			commandLine.add(tokenizer.sval);
		}
		next = tokenizer.nextToken();
		if (next != SaverStory.ARGUMENT_END) {
			throw new CommandException(tokenizer.lineno(), "セリフ", -1, "引数が閉じられていません。");
		}
	}
}
