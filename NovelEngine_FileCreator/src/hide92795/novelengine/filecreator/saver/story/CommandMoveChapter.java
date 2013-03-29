package hide92795.novelengine.filecreator.saver.story;

import hide92795.novelengine.filecreator.CommandException;
import hide92795.novelengine.filecreator.VarNumManager;
import hide92795.novelengine.filecreator.saver.SaverStory;
import java.io.StreamTokenizer;
import java.util.LinkedList;

public class CommandMoveChapter extends Command {

	@Override
	public void save(StreamTokenizer tokenizer, LinkedList<Object> commandLine) throws Exception {
		// 文字列 チャプターID
		commandLine.add(SaverStory.COMMAND_MOVE_CHAPTER);
		int next = tokenizer.nextToken();
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "移動", 1, "引数「チャプターID」は文字列でなければいけません。");
		} else {
			commandLine.add(VarNumManager.CHAPTER_ID.add(tokenizer.sval));
		}
		next = tokenizer.nextToken();
		if (next != SaverStory.ARGUMENT_END) {
			throw new CommandException(tokenizer.lineno(), "移動", -1, "引数が閉じられていません。");
		}
	}

}
