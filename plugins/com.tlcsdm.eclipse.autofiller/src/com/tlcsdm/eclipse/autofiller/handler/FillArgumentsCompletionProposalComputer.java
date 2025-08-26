package com.tlcsdm.eclipse.autofiller.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.tlcsdm.eclipse.autofiller.generator.ArgumentFiller;

public class FillArgumentsCompletionProposalComputer implements IJavaCompletionProposalComputer {

	@Override
	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {

		List<ICompletionProposal> proposals = new ArrayList<>();
		int offset = context.getInvocationOffset();
		IDocument doc = context.getDocument();

		try {
			IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if (!(editor instanceof ITextEditor)) {
				return Collections.emptyList();
			}

			ITextEditor textEditor = (ITextEditor) editor;
			IEditorInput input = textEditor.getEditorInput();
			ICompilationUnit icu = JavaUI.getWorkingCopyManager().getWorkingCopy(input);
			if (icu == null) {
				return Collections.emptyList();
			}

			ArgumentFiller filler = new ArgumentFiller(doc, offset, icu);
			String replacement = filler.previewArguments();
			if (replacement != null) {
				CompletionProposal proposal = new CompletionProposal(replacement, filler.getReplaceOffset(),
						filler.getReplaceLength(), filler.getReplaceOffset() + replacement.length(), null,
						"Fill method arguments: " + replacement, null, null);
				proposals.add(proposal);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return proposals;
	}

	@Override
	public void sessionStarted() {
		// Do nothing
	}

	@Override
	public void sessionEnded() {
		// Do nothing
	}

	@Override
	public List<IContextInformation> computeContextInformation(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {
		return Collections.emptyList();
	}

	@Override
	public String getErrorMessage() {
		return null;
	}
}
