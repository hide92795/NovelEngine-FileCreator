package hide92795.novelengine.filecreator.saver.story;

import hide92795.novelengine.filecreator.CommandException;
import hide92795.novelengine.filecreator.saver.SaverStory;
import java.io.StreamTokenizer;
import java.util.LinkedList;

public class CommandAssignment extends Command {
	@Override
	public void save(StreamTokenizer tokenizer, LinkedList<Object> commandLine) throws Exception {
		// String 変数名, int 設定値
		commandLine.add(SaverStory.COMMAND_SET_VARIABLE);
		int next = tokenizer.nextToken();
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "設定", 1, "引数「変数名」は文字列でなければいけません。");
		} else {
			String[] var = tokenizer.sval.split("\\.");
			int varType = SaverStory.parseVariableType(var[0]);
			commandLine.add(varType);
			commandLine.add(var[1]);
		}
		next = nextArgument(tokenizer);
		if (next != StreamTokenizer.TT_NUMBER) {
			// 非数値
			throw new CommandException(tokenizer.lineno(), "設定", 2, "引数「設定値」は数値でなければいけません。");
		} else {
			int i = (int) tokenizer.nval;
			commandLine.add(i);
		}
		next = tokenizer.nextToken();
		if (next != SaverStory.ARGUMENT_END) {
			throw new CommandException(tokenizer.lineno(), "設定", -1, "引数が閉じられていません。");
		}
	}
}
