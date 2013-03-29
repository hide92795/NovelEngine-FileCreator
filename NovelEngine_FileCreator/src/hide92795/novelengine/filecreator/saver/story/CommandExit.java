package hide92795.novelengine.filecreator.saver.story;

import hide92795.novelengine.filecreator.CommandException;
import hide92795.novelengine.filecreator.saver.SaverStory;
import java.io.StreamTokenizer;
import java.util.LinkedList;

public class CommandExit extends Command {
	@Override
	public void save(StreamTokenizer tokenizer, LinkedList<Object> commandLine) throws Exception {
		// boolean 終了確認
		commandLine.add(SaverStory.COMMAND_EXIT);
		int next = tokenizer.nextToken();
		if (next != StreamTokenizer.TT_WORD) {
			// 非真偽値
			throw new CommandException(tokenizer.lineno(), "終了", 1, "引数「終了確認」は真偽値でなければいけません。");
		} else {
			commandLine.add(Boolean.parseBoolean(tokenizer.sval));
		}
		next = tokenizer.nextToken();
		if (next != SaverStory.ARGUMENT_END) {
			throw new CommandException(tokenizer.lineno(), "終了", -1, "引数が閉じられていません。");
		}
	}
}
