package hide92795.novelengine.filecreator;

public class CommandException extends Exception {
	private static final long serialVersionUID = 1556114574280625817L;

	public CommandException(int line, String commandName, int argNumber, String desc) {
		super(new StringBuilder().append("コマンド解析中にエラーが発生しました。\n行番号：").append(line).append("\nコマンド名：")
				.append(commandName).append("\n引数：").append(argNumber).append("\n").append(desc).toString());
	}
}
