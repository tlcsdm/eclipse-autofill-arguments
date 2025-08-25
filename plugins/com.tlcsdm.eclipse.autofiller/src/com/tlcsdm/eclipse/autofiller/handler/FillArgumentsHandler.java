package com.tlcsdm.eclipse.autofiller.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import com.tlcsdm.eclipse.autofiller.generator.ArgumentFiller;

public class FillArgumentsHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (!(editor instanceof ITextEditor))
			return null;

		ITextEditor textEditor = (ITextEditor) editor;
		IDocument doc = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());

		ISelection sel = textEditor.getSelectionProvider().getSelection();
		if (!(sel instanceof ITextSelection))
			return null;

		ITextSelection ts = (ITextSelection) sel;
		int offset = ts.getOffset();

		// 调用参数生成逻辑
		new ArgumentFiller(doc, offset).fillArguments();

		return null;
	}
}