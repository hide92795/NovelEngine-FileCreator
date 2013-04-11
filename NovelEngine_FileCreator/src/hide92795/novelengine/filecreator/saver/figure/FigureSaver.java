package hide92795.novelengine.filecreator.saver.figure;

import java.util.Map;
import org.msgpack.packer.Packer;

public interface FigureSaver {
	void save(Map<?, ?> map_f, Packer packer) throws Exception;
}
