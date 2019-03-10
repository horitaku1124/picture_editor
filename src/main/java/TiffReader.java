import util.BinaryReader;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.lang.System.out;

public class TiffReader {
  public static void main(String[] args) throws IOException {
    final String fileName = "./images/SECTION69.tiff";

    byte[] data = Files.readAllBytes(Paths.get(fileName));

    BinaryReader binRead;
    if (data[0] == 0x49 && data[0] == data[1]) {
      binRead = new BinaryReader(BinaryReader.Endian.Little);
    } else if (data[0] == 0x4d && data[0] == data[1]) {
      binRead = new BinaryReader(BinaryReader.Endian.Big);
    } else {
      throw new RuntimeException("Could'nt recognize magic");
    }

    short magic = binRead.bytesToShort(data, 0x0002);
    if (magic != 0x002a && magic != 0x002b) { // 0x002a=TIFF nor 0x002b=BigTIFF
      throw new RuntimeException("Could'nt recognize magic");
    }
    var tiffOffset = binRead.bytesToLong(data, 0x0004);
    out.format("TIFF.offset = 0x%08x\n", tiffOffset);

    out.println(" -- IFD0 --");

    short numberOfEntries = binRead.bytesToShort(data, tiffOffset);
    out.format("numberOfEntries=%04x\n", numberOfEntries);


    for (int i = 0;i < numberOfEntries;i++) {
      long offset = tiffOffset + i * 12 + 2;
      short tagId = binRead.bytesToShort(data, offset);
      short tagType = binRead.bytesToShort(data, offset + 2);
      long tagValue = 0;
      if (tagType == CR2Reader.IFD_TAG_TYPE.SHORT) {
        tagValue = binRead.bytesToUnsignedShort(data, offset + 4);
      }
//        out.format("offset=%04x tagId=%d tagType=%d tagValue=%d\n", offset, tagId, tagType, tagValue);
      long tagData = 0;

      if (tagType == CR2Reader.IFD_TAG_TYPE.SHORT) {
        tagData = binRead.bytesToUnsignedShort(data, offset + 8);
      } else if (tagType == CR2Reader.IFD_TAG_TYPE.LONG) {
        tagData = binRead.bytesToLong(data, offset + 8);
      } else {
        tagData = binRead.bytesToLong(data, offset + 8);
      }

      if (tagId == IFD_TAG.WIDTH) {
        out.format("ifd0.width=%d\n", tagData);
      } else if (tagId == IFD_TAG.HEIGHT) {
        out.format("ifd0.height=%d\n", tagData);
      }
    }
  }
}
