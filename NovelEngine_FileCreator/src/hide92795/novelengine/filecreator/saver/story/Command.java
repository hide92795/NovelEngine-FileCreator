package hide92795.novelengine.filecreator.saver.story;

import hide92795.novelengine.filecreator.CommandException;
import hide92795.novelengine.filecreator.saver.SaverStory;
import java.io.StreamTokenizer;
import java.util.LinkedList;

public abstract class Command {
	public abstract void save(StreamTokenizer tokenizer, LinkedList<Object> commandLine) throws Exception;

	protected int nextArgument(StreamTokenizer tokenizer) throws Exception {
		int token = tokenizer.nextToken();
		if (token != SaverStory.COMMA) {
			throw new CommandException(tokenizer.lineno(), "区切り文字", -1, "引数が不足している、もしくは区切り文字が不正です。");
		}
		token = tokenizer.nextToken();
		return token;
	}
}
