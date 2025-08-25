package com.tlcsdm.eclipse.autofiller.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;

public class FillArgumentsCompletionProposalComputer implements IJavaCompletionProposalComputer {
	@Override
	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {

		List<ICompletionProposal> proposals = new ArrayList<>();
		int offset = context.getInvocationOffset();
		IDocument doc = context.getDocument();

		proposals.add(new CompletionProposal("autoFillArgs()", // 替换文本（实际调用 ArgumentFiller 生成）
				offset, 0, "autoFillArgs()".length(), null, "Fill Method Arguments", null, null));

		return proposals;
	}

	@Override
	public void sessionStarted() {
	}

	@Override
	public void sessionEnded() {
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
