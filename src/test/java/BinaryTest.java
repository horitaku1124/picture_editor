import org.junit.Test;

public class BinaryTest {
  @Test
  public void test1() {
    byte[] data = new byte[]{0x3e, (byte)0xAD, 0x00, 0x00};


    for (byte b: data) {
      System.out.format("%x\n", b);
    }

    long ret = 0;
    int offset = 0;
    ret += data[offset] & 0xff;
    ret += (data[offset + 1] & 0xff) << 8;
    ret += (data[offset + 2] & 0xff) << 16;
    ret += (data[offset + 3] & 0xff) << 24;
    System.out.format("%08x\n", ret);
  }
}
