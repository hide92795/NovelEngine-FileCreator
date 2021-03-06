//
// NovelEngine Project
//
// Copyright (C) 2013 - hide92795
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package hide92795.novelengine.filecreator.saver.story;

import hide92795.novelengine.filecreator.CommandException;
import hide92795.novelengine.filecreator.saver.SaverStory;
import java.io.StreamTokenizer;
import java.util.LinkedList;

/**
 * 「計算」コマンドを解析するローダーです。
 * 
 * @author hide92795
 */
public class CommandCalculation extends Command {
	@Override
	public void save(StreamTokenizer tokenizer, LinkedList<Object> commandLine) throws Exception {
		// String 変数名, String 計算式
		commandLine.add(SaverStory.COMMAND_CALCULATION);
		int next = tokenizer.nextToken();
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "計算", 1, "引数「変数名」は文字列でなければいけません。");
		} else {
			String[] var = tokenizer.sval.split("\\.");
			int varType = SaverStory.parseVariableType(var[0]);
			commandLine.add(varType);
			commandLine.add(Integer.parseInt(var[1]));
		}
		next = nextArgument(tokenizer);
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "計算", 2, "引数「計算式」は文字列でなければいけません。");
		} else {
			String operation = SaverStory.searchOperation(tokenizer.sval);
			String[] args = tokenizer.sval.split(operation);
			if (args.length != 2) {
				// 条件不足
				throw new CommandException(tokenizer.lineno(), "計算", 2, "引数「計算式」が不正です。");
			}
			commandLine.add(SaverStory.parseOperation(operation));
			SaverStory.parseVariable(args[0], commandLine);
			SaverStory.parseVariable(args[1], commandLine);
		}
	}
}
