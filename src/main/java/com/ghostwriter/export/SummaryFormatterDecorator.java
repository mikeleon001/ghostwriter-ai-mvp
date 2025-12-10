package com.ghostwriter.export;

import com.ghostwriter.analysis.Summary;

/**
 * Abstract Decorator in the Decorator design pattern.
 * Base class for all concrete decorators that add formatting behavior.
 * 
 * This class implements SummaryFormatter and holds a reference to another
 * SummaryFormatter, allowing decorators to wrap and enhance the behavior
 * of the wrapped formatter.
 * 
 * @author Mihail Chitorog
 * @version 1.0
 */
public abstract class SummaryFormatterDecorator implements SummaryFormatter {
    
    /**
     * The wrapped formatter that this decorator enhances.
     * Protected so subclasses can access it if needed.
     */
    protected SummaryFormatter wrappedFormatter;
    
    /**
     * Constructs a decorator that wraps another formatter.
     * 
     * @param formatter The formatter to wrap/decorate
     * @throws IllegalArgumentException if formatter is null
     */
    public SummaryFormatterDecorator(SummaryFormatter formatter) {
        if (formatter == null) {
            throw new IllegalArgumentException("Wrapped formatter cannot be null");
        }
        this.wrappedFormatter = formatter;
    }
    
    /**
     * Default implementation delegates to the wrapped formatter.
     * Concrete decorators can override this to add their own behavior.
     * 
     * @param summary The summary to format
     * @return Formatted string from the wrapped formatter
     */
    @Override
    public String format(Summary summary) {
        return wrappedFormatter.format(summary);
    }
    
    /**
     * Default implementation delegates to the wrapped formatter.
     * Concrete decorators should override this to return their own extension.
     * 
     * @return File extension from the wrapped formatter
     */
    @Override
    public String getFileExtension() {
        return wrappedFormatter.getFileExtension();
    }
}
