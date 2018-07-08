package fr.skyost.skydocs;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public abstract class DocsRunnable<T> {

	private final HashSet<RunnableListener> listeners = new HashSet<>();

	private PrintStream out;
	private Scanner in;

	private boolean isInterrupted = true;
	private DocsRunnable[] subTasks;

	public DocsRunnable() {
		this(System.out);
	}

	public DocsRunnable(final PrintStream out) {
		this(out, System.in);
	}

	public DocsRunnable(final PrintStream out, final InputStream in) {
		this(out, in, new DocsRunnable[0]);
	}

	public DocsRunnable(final PrintStream out, final InputStream in, final DocsRunnable... subTasks) {
		this.out = out;
		setInputStream(in);

		this.subTasks = subTasks;
	}

	public final void addListeners(final RunnableListener... listeners) {
		this.listeners.addAll(Arrays.asList(listeners));
	}

	public final void removeListener(final RunnableListener listener) {
		listeners.remove(listener);
	}

	public final void clearListeners() {
		listeners.clear();
	}

	public boolean canOutput() {
		return out != null;
	}

	public PrintStream getOutputStream() {
		return out;
	}

	public void setOutputStream(final PrintStream out) {
		this.out = out;
	}

	public void output(final String message) {
		if(out == null) {
			return;
		}

		out.print(message);
	}

	public void outputLine(final String message) {
		if(out == null) {
			return;
		}

		out.println(message);
	}

	public void blankLine() {
		if(out == null) {
			return;
		}

		out.println();
	}

	public boolean canInput() {
		return in != null;
	}

	public Scanner getScanner() {
		return in;
	}

	public void setInputStream(final InputStream in) {
		this.in = in == null ? null : new Scanner(in, StandardCharsets.UTF_8.name());
	}

	public String inputLine() {
		if(in == null) {
			return null;
		}

		if(in.hasNextLine() && !isInterrupted) {
			return in.nextLine();
		}

		return null;
	}

	public DocsRunnable[] getSubTasks() {
		return subTasks;
	}

	public void setSubTasks(final DocsRunnable... subTasks) {
		this.subTasks = subTasks;
	}

	public void interrupt() {
		isInterrupted = true;

		for(final DocsRunnable subTask : subTasks) {
			subTask.interrupt();
		}

		if(in != null) {
			in.close();
		}
	}

	public boolean isInterrupted() {
		return isInterrupted;
	}

	/**
	 * Exits if interrupted.
	 *
	 * @throws InterruptionException The interruption signal.
	 */

	protected void exitIfInterrupted() throws InterruptionException {
		if(!isInterrupted) {
			return;
		}
		throw new InterruptionException();
	}

	public T run() {
		return run(true);
	}

	public T run(final boolean showTime) {
		try {
			isInterrupted = false;
			final long first = System.currentTimeMillis();

			final T result = execute();
			if(result == null) {
				return null;
			}

			final long second = System.currentTimeMillis();
			if(showTime) {
				outputLine("Done in " + (second - first) / 1000f + " seconds !");
			}
			interrupt();

			return result;
		}
		catch(final Exception ex) {
			blankLine();
			outputLine("An exception occurred while executing the command :");
			ex.printStackTrace(out);

			if(out != System.out) {
				ex.printStackTrace();
			}
		}

		return null;
	}

	protected abstract T execute() throws Exception;

	public interface RunnableListener {

		void onRunnableStarted(final DocsRunnable command);
		void onRunnableFinished(final DocsRunnable command);
		void onRunnableError(final DocsRunnable command, final Throwable error);

	}

	/**
	 * The interruption signal.
	 */

	public static class InterruptionException extends Exception {

		private static final long serialVersionUID = 1L;

		public InterruptionException() {
			super("Interrupted !");
		}

	}

}