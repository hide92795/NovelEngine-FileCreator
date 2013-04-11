package hide92795.novelengine.filecreator.saver;

import hide92795.novelengine.filecreator.VarNumManager;
import hide92795.novelengine.filecreator.saver.figure.FigureSaver;
import hide92795.novelengine.filecreator.saver.figure.FigureSaverCircle;
import hide92795.novelengine.filecreator.saver.figure.FigureSaverPolygon;
import hide92795.novelengine.filecreator.saver.figure.FigureSaverQuadrangle;
import hide92795.novelengine.filecreator.saver.figure.FigureSaverTriangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.crypto.CipherOutputStream;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.yaml.snakeyaml.Yaml;

public class SaverFigure extends Saver {
	private static HashMap<String, FigureSaver> figureSavers;

	static {
		figureSavers = new HashMap<String, FigureSaver>();
		figureSavers.put("三角形", new FigureSaverTriangle());
		figureSavers.put("四角形", new FigureSaverQuadrangle());
		figureSavers.put("多角形", new FigureSaverPolygon());
		figureSavers.put("円形", new FigureSaverCircle());
	}

	public static final byte FIGURE_ENTIRE_SCREEN = 0;
	public static final byte FIGURE_TRIANGLE = 1;
	public static final byte FIGURE_QUADANGLE = 2;
	public static final byte FIGURE_POLYGON = 3;
	public static final byte FIGURE_CIRCLE = 4;

	private File path;

	public SaverFigure(File output, Properties crypt, String encoding, File path) {
		super(output, crypt, encoding);
		this.path = path;
	}

	@Override
	public void pack() throws Exception {
		CipherOutputStream cos = createCipherInputStream(new File(output, "figure.neo"), crypt);

		MessagePack msgpack = new MessagePack();
		Packer p = msgpack.createPacker(cos);
		Yaml yaml = new Yaml();

		HashMap<String, Integer> figures_map = VarNumManager.FIGURE.getMap();
		Set<String> figures_ks = figures_map.keySet();

		p.write(figures_ks.size());

		for (String figures_s : figures_ks) {
			p.write(figures_map.get(figures_s));

			File character = new File(path, figures_s + ".yml");
			BufferedReader reader_f = new BufferedReader(
					new InputStreamReader(new FileInputStream(character), encoding));
			Map<?, ?> map_f = (Map<?, ?>) yaml.load(reader_f);

			String type = map_f.get("Type").toString();
			if (type != null) {
				FigureSaver figureSaver = SaverFigure.figureSavers.get(type);
				figureSaver.save(map_f, p);
			}
			reader_f.close();
		}

		p.flush();
		p.close();
	}

}
