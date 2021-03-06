import edu.princeton.cs.algs4.StdIn;

public class BurrowsWheeler
{
	private final static int R = 256;

	private static int getFirst(CircularSuffixArray csa)
	{
		for (int i = 0; i < csa.length(); i++)
		{
			if (csa.index(i) == 0)
				return i;
		}

		return -1;
	}

	// apply Burrows-Wheeler transform, reading from standard input and writing
	// to standard output
	public static void encode()
	{
		String data = StdIn.readAll();
		CircularSuffixArray csa = new CircularSuffixArray(data);
		int first = getFirst(csa);

		// Write first to stdout, using big endian
		for (int i = 24; i >= 0; i -= 8)
			System.out.write((first >> i) & 0xFF);

		final byte[] buf = data.getBytes();
		final int len = buf.length;
		// Write the end of each element in sorted suffix
		for (int i = 0; i < len; i++)
			System.out.write(buf[(len - 1 + csa.index(i)) % len]);
	}

	// apply Burrows-Wheeler inverse transform, reading from standard input and
	// writing to standard output
	public static void decode()
	{
		// The first 4 bytes of input stream should be the
		// first index
		int first = 0;
		for (int i = 0; i < 4; i++)
			first = (first << 8) | (StdIn.readByte());

		// Buffer that holds all transformed data
		byte[] a = StdIn.readAll().getBytes();
		final int N = a.length;

		// the next table
		int next[] = new int[N];

		// Apply "LSD Sort", takes O(N) time and O(R) space,
		byte[] aux = new byte[N];
		int[] count = new int[R + 1];
		for (byte b : a)
			count[(b & 0xFF) + 1]++;

		// calculate cumulative
		for (int i = 0; i < R; i++)
			count[i + 1] += count[i];

		// move data, but do the same operations to index of elements in the
		// unsorted array.
		// the resulting next stores the next table we want
		for (int i = 0; i < N; i++)
		{
			byte b = a[i];
			aux[count[b & 0xFF]] = b;
			next[count[b & 0xFF]] = i;
			count[b & 0xFF]++;
		}

		for (int i = 0; i < N; i++)
		{
			System.out.write(aux[first] & 0xFF);
			first = next[first];
		}
	}

	// if args[0] is '-', apply Burrows-Wheeler transform
	// if args[0] is '+', apply Burrows-Wheeler inverse transform
	public static void main(String[] args)
	{
		if (args[0].equals("-"))
			encode();
		else if (args[0].equals("+"))
			decode();
		System.out.close();
	}
}