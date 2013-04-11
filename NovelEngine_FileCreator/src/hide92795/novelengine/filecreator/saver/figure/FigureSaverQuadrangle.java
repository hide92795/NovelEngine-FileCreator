package hide92795.novelengine.filecreator.saver.figure;

import hide92795.novelengine.filecreator.saver.SaverFigure;
import java.awt.Color;
import java.util.Map;
import java.util.Set;
import org.msgpack.packer.Packer;

public class FigureSaverQuadrangle implements FigureSaver {
	@Override
	public void save(Map<?, ?> map_f, Packer packer) throws Exception {
		packer.write(SaverFigure.FIGURE_QUADANGLE);
		Map<?, ?> apexes = (Map<?, ?>) map_f.get("Apexes");
		for (int i = 0; i < 4; i++) {
			Map<?, ?> apex = (Map<?, ?>) apexes.get(i + 1);
			int x = (Integer) apex.get("x");
			int y = (Integer) apex.get("y");
			packer.write(x);
			packer.write(y);
		}
		Map<?, ?> lines = (Map<?, ?>) map_f.get("Lines");
		if (lines != null) {
			Set<?> set = lines.keySet();
			int num = set.size();
			packer.write(num);
			for (Object line_o : set) {
				String line = line_o.toString();
				String[] lineEnds = line.split("-");
				int start = Integer.valueOf(lineEnds[0]);
				int end = Integer.valueOf(lineEnds[1]);

				Map<?, ?> lineInfo = (Map<?, ?>) lines.get(line_o);
				String color_s = lineInfo.get("Color").toString();
				int color = Color.decode("#" + color_s).getRGB();
				byte alpha = ((Integer) lineInfo.get("Alpha")).byteValue();
				int width = (Integer) lineInfo.get("Width");

				packer.write(start - 1);
				packer.write(end - 1);
				packer.write(color);
				packer.write(alpha);
				packer.write(width);
			}
		}
	}
}
