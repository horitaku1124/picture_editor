
public class BinaryReader {
  enum Endian {
    Little,
    Big
  }
  private Endian endianness;
  public BinaryReader(Endian endian) {
    this.endianness = endian;
  }

  public short bytesToShort(byte[] data, long _offset) {
    int offset = (int) _offset;
    int ret = 0;
    if (endianness == Endian.Little) {
      ret += data[offset];
      ret += data[offset + 1] << 8;
    } else {
      ret += data[offset] << 8;
      ret += data[offset + 1];
    }
    return (short)ret;
  }
  public short bytesToUnsignedShort(byte[] data, long _offset) {
    int offset = (int) _offset;
    long ret = 0;
    if (endianness == Endian.Little) {
      ret += data[offset] & 0xff;
      ret += (data[offset + 1] & 0xff) << 8;
    } else {
      ret += data[offset] & 0xff << 24;
      ret += (data[offset + 1] & 0xff) << 16;
    }
    return (short) ret;
  }
  public long bytesToLong(byte[] data, long _offset) {
    int offset = (int) _offset;
    long ret = 0;
    if (endianness == Endian.Little) {
      ret += data[offset] & 0xff;
      ret += (data[offset + 1] & 0xff) << 8;
      ret += (data[offset + 2] & 0xff) << 16;
      ret += (data[offset + 3] & 0xff) << 24;
    } else {
      ret += data[offset] & 0xff << 24;
      ret += (data[offset + 1] & 0xff) << 16;
      ret += (data[offset + 2] & 0xff) << 8;
      ret += (data[offset + 3] & 0xff);
    }
    return ret;
  }
}