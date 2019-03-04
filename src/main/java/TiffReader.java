import util.BinaryReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.lang.System.out;

public class TiffReader {
  public static void main(String[] args) throws IOException {
    final String fileName = "./images/SECTION69.tiff";

    try (var is = Files.newInputStream(Paths.get(fileName))) {
      var data = new byte[8];
      is.read(data);

      BinaryReader binRead;
      if (data[0] == 0x49 && data[0] == data[1]) {
        binRead = new BinaryReader(BinaryReader.Endian.Little);
      } else if (data[0] == 0x4d && data[0] == data[1]) {
        binRead = new BinaryReader(BinaryReader.Endian.Big);
      } else {
        throw new RuntimeException("Could'nt recognize magic");
      }

      short magic = binRead.bytesToShort(data, 0x0002);
      if (magic != 0x002a) {
        throw new RuntimeException("Could'nt recognize magic");
      }
      var tiffOffset = binRead.bytesToLong(data, 0x0004);
      out.format("TIFF.offset = 0x%08x\n", tiffOffset);

      data = new byte[(int) (tiffOffset - 8)];
      var len = is.read(data);
      out.format("%d=%d\n", len, data.length);
    }
  }
}
