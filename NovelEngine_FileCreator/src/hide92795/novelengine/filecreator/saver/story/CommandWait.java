package hide92795.novelengine.filecreator.saver.story;

import hide92795.novelengine.filecreator.CommandException;
import hide92795.novelengine.filecreator.saver.SaverStory;
import java.io.StreamTokenizer;
import java.util.LinkedList;

public class CommandWait extends Command {
	@Override
	public void save(StreamTokenizer tokenizer, LinkedList<Object> commandLine) throws Exception {
		// int 待機時間, String 単位
		commandLine.add(SaverStory.COMMAND_WAIT);
		int next = tokenizer.nextToken();
		int time = 0;
		if (next != StreamTokenizer.TT_NUMBER) {
			// 非数値
			throw new CommandException(tokenizer.lineno(), "待機", 1, "引数「待機時間」は数値でなければいけません。");
		} else {
			time = (int) tokenizer.nval;
		}
		next = nextArgument(tokenizer);
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "待機", 2, "引数「単位」は文字列でなければいけません。");
		} else {
			String prefix = tokenizer.sval;
			if (prefix.toLowerCase().equals("s")) {
				// 秒
				commandLine.add(time * 1000);
			} else if (prefix.toLowerCase().equals("ms")) {
				// ミリ秒
				commandLine.add(time);
			} else {
				throw new CommandException(tokenizer.lineno(), "待機", 2, "引数「単位」は「s(秒)」か「ms(ミリ秒)」でなければいけません。");
			}
		}
		next = tokenizer.nextToken();
		if (next != SaverStory.ARGUMENT_END) {
			throw new CommandException(tokenizer.lineno(), "待機", -1, "引数が閉じられていません。");
		}
	}
}
