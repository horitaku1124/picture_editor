import util.BinaryReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CR2Reader {
  interface IFD_TAG_TYPE {
    int SHORT = 3;
  }
  interface IFD_TAG_ID {
    int WIDTH = 0x0100;
    int HEIGHT = 0x0101;
  }
  public static void main(String[] args) {
    final String fileName = "./images/IMG_2631.CR2";

    File file = new File(fileName);
    ByteBuffer bb = ByteBuffer.allocate((int)file.length());
    try (FileInputStream fis = new FileInputStream(file)) {
      byte[] data = new byte[16];
      fis.read(data, 0, 16);

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

      long tiffOffset = binRead.bytesToLong(data, 0x0004);
      System.out.format("TIFF.offset = 0x%08x\n", tiffOffset);

      short cr2Magic = binRead.bytesToShort(data, 0x0008);
      if (cr2Magic != 0x5243) {
        throw new RuntimeException("Could'nt recognize magic");
      }
      byte cr2MajorVersion = data[0x000a];
      byte cr2minorVersion = data[0x000b];
      System.out.format("CR2.version = %d.%d\n", cr2MajorVersion, cr2minorVersion);
      long ifd3Offset = binRead.bytesToLong(data, 0x000c);
      System.out.format("IFD3.offset = %08x\n", ifd3Offset);

      bb.put(data);

      while (true) {
        byte[] temp = new byte[100000];
        int length = fis.read(temp, 0, temp.length);
        if (length <= 0) {
          break;
        }
        bb.put(temp, 0, length);
      }

      System.out.println(" -- IFD0 --");

      byte[] allData = bb.array();

      short numberOfEntries = binRead.bytesToShort(allData, tiffOffset);
      System.out.format("numberOfEntries=%04x\n", numberOfEntries);
      for (int i = 0;i < numberOfEntries;i++) {
        long offset = tiffOffset + i * 12 + 2;
        short tagId = binRead.bytesToShort(allData, offset);
        short tagType = binRead.bytesToShort(allData, offset + 2);
        long tagValue = 0;
        if (tagType == IFD_TAG_TYPE.SHORT) {
          tagValue = binRead.bytesToUnsignedShort(allData, offset + 4);
        }
        long tagData = 0;

        if (tagType == IFD_TAG_TYPE.SHORT) {
          tagData = binRead.bytesToUnsignedShort(allData, offset + 8);
        }
        if (tagId == IFD_TAG_ID.WIDTH) {
          System.out.format("ifd0.width=%d\n", tagData);
        } else if (tagId == IFD_TAG_ID.HEIGHT) {
          System.out.format("ifd0.height=%d\n", tagData);
        }
//                System.out.format("tagId=%04x tagType=%d tagValue=%d tagData=%d\n", tagId, tagType, tagValue, tagData);

      }



      System.out.println(" -- IFD3 --");
      short tagId = binRead.bytesToShort(allData, ifd3Offset);
      short tagType = binRead.bytesToShort(allData, ifd3Offset + 2);
      long tagNumber = binRead.bytesToLong(allData, ifd3Offset + 4);
      long nextPointer = binRead.bytesToLong(allData, ifd3Offset + 8);
      System.out.format("tagId=%04x\n", tagId);
      System.out.format("tagType=%04x\n", tagType);
      System.out.format("tagNumber=%08x\n", tagNumber);
      System.out.format("nextPointer=%08x\n", nextPointer);


    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
