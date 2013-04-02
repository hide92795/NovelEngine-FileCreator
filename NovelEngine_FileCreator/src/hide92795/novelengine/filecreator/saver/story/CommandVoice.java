package hide92795.novelengine.filecreator.saver.story;

import hide92795.novelengine.filecreator.CommandException;
import hide92795.novelengine.filecreator.VarNumManager;
import hide92795.novelengine.filecreator.saver.SaverStory;
import java.io.StreamTokenizer;
import java.util.LinkedList;

public class CommandVoice extends Command {
	@Override
	public void save(StreamTokenizer tokenizer, LinkedList<Object> commandLine) throws Exception {
		// String キャラクターID, String ボイスID
		commandLine.add(SaverStory.COMMAND_VOICE);
		String characterName;
		int next = tokenizer.nextToken();
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "ボイス", 1, "引数「キャラクターID」は文字列でなければいけません。");
		} else {
			characterName = tokenizer.sval;
			commandLine.add(VarNumManager.CHARACTER.add(characterName));
		}
		next = nextArgument(tokenizer);
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "ボイス", 2, "引数「ボイスID」は文字列でなければいけません。");
		} else {
			String voiceId = characterName + "." + tokenizer.sval;
			commandLine.add(VarNumManager.VOICE.add(voiceId));
		}
		next = tokenizer.nextToken();
		if (next != SaverStory.ARGUMENT_END) {
			throw new CommandException(tokenizer.lineno(), "ボイス", -1, "引数が閉じられていません。");
		}

	}
}
