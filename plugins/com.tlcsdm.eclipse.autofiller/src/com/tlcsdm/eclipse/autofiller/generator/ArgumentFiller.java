package com.tlcsdm.eclipse.autofiller.generator;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

public class ArgumentFiller {
	private final IDocument doc;
	private final int offset;

	public ArgumentFiller(IDocument doc, int offset) {
		this.doc = doc;
		this.offset = offset;
	}

	public void fillArguments() {
		try {
			// 假设拿到方法参数类型
			String[] args = { "\"\"", "0", "false" };

			String replacement = String.join(", ", args);
			doc.replace(offset, 0, replacement);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
