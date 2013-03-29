package hide92795.novelengine.filecreator.saver.story;

import hide92795.novelengine.filecreator.CommandException;
import hide92795.novelengine.filecreator.saver.SaverStory;
import java.io.StreamTokenizer;
import java.util.LinkedList;

public class CommandBackGroundEffect extends Command {
	@Override
	public void save(StreamTokenizer tokenizer, LinkedList<Object> commandLine) throws Exception {
		// byte 対象, int 遅延（ms）, エフェクト エフェクト
		commandLine.add(SaverStory.COMMAND_EFFECT_BACKGROUND);
		int next = tokenizer.nextToken();
		if (next != StreamTokenizer.TT_NUMBER) {
			// 非数値
			throw new CommandException(tokenizer.lineno(), "背景エフェクト", 1, "引数「対象」は数値でなければいけません。");
		} else {
			byte i = (byte) tokenizer.nval;
			commandLine.add(i);
		}
		next = nextArgument(tokenizer);
		if (next != StreamTokenizer.TT_NUMBER) {
			// 非数値
			throw new CommandException(tokenizer.lineno(), "背景エフェクト", 2, "引数「遅延」は数値でなければいけません。");
		} else {
			int i = (int) tokenizer.nval;
			commandLine.add(i);
		}
		next = nextArgument(tokenizer);
		if (next != StreamTokenizer.TT_WORD && tokenizer.sval.equals("エフェクト")) {
			throw new CommandException(tokenizer.lineno(), "背景エフェクト", 3, "引数「エフェクト」はエフェクトコマンドでなければいけません。");
		}
		next = tokenizer.nextToken();
		if (next != SaverStory.ARGUMENT_START) {
			throw new CommandException(tokenizer.lineno(), "背景エフェクト", 3, "引数「エフェクト」の記述が不正です。");
		}
		// 背景エフェクト
		next = tokenizer.nextToken();
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "エフェクト", 1, "引数「エフェクトID」は文字列でなければいけません。");
		} else {
			commandLine.add(tokenizer.sval.hashCode());
		}
		while ((next = tokenizer.nextToken()) != SaverStory.ARGUMENT_END) {
			switch (next) {
			case StreamTokenizer.TT_EOF:
			case SaverStory.BLOCK_END:
			case SaverStory.END:
				throw new CommandException(tokenizer.lineno(), "エフェクト", -1, "コマンド内に不正な文字があります。");
			case StreamTokenizer.TT_NUMBER:
				int i = (int) tokenizer.nval;
				commandLine.add(i);
				break;
			case StreamTokenizer.TT_WORD:
				commandLine.add(Boolean.parseBoolean(tokenizer.sval));
				break;
			case SaverStory.DOUBLE_QUOTE:
				commandLine.add(tokenizer.sval.hashCode());
				break;
			}
		}
		next = tokenizer.nextToken();
		if (next != SaverStory.ARGUMENT_END) {
			throw new CommandException(tokenizer.lineno(), "エフェクト", -1, "引数が閉じられていません。");
		}
	}
}
