package expresslibrary.logic.parser;

import static expresslibrary.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static expresslibrary.logic.parser.CliSyntax.PREFIX_AUTHOR;
import static expresslibrary.logic.parser.CliSyntax.PREFIX_BORROW_DATE;
import static expresslibrary.logic.parser.CliSyntax.PREFIX_DUE_DATE;
import static expresslibrary.logic.parser.CliSyntax.PREFIX_ISBN;
import static expresslibrary.logic.parser.CliSyntax.PREFIX_TITLE;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import expresslibrary.commons.core.index.Index;
import expresslibrary.commons.util.DateUtil;
import expresslibrary.logic.commands.EditBookCommand;
import expresslibrary.logic.commands.EditBookCommand.EditBookDescriptor;
import expresslibrary.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EditBookCommand object
 */
public class EditBookCommandParser implements Parser<EditBookCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the
     * EditBookCommand
     * and returns an EditBookCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditBookCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TITLE, PREFIX_AUTHOR, PREFIX_ISBN,
                PREFIX_BORROW_DATE, PREFIX_DUE_DATE);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditBookCommand.MESSAGE_USAGE), pe);
        }

        EditBookDescriptor editBookDescriptor = new EditBookDescriptor();
        if (argMultimap.getValue(PREFIX_TITLE).isPresent()) {
            editBookDescriptor.setTitle(ParserUtil.parseTitle(argMultimap.getValue(PREFIX_TITLE).get()));
        }
        if (argMultimap.getValue(PREFIX_AUTHOR).isPresent()) {
            editBookDescriptor.setAuthor(ParserUtil.parseAuthor(argMultimap.getValue(PREFIX_AUTHOR).get()));
        }
        if (argMultimap.getValue(PREFIX_ISBN).isPresent()) {
            editBookDescriptor.setIsbn(ParserUtil.parseIsbn(argMultimap.getValue(PREFIX_ISBN).get()));
        }
        if (argMultimap.getValue(PREFIX_BORROW_DATE).isPresent()) {
            editBookDescriptor.setBorrowDate(DateUtil.parseDate(argMultimap.getValue(PREFIX_BORROW_DATE).get()));
        }
        if (argMultimap.getValue(PREFIX_DUE_DATE).isPresent()) {
            editBookDescriptor.setDueDate(DateUtil.parseDate(argMultimap.getValue(PREFIX_DUE_DATE).get()));
        }
        if (!editBookDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditBookCommand.MESSAGE_NOT_EDITED);
        }

        return new EditBookCommand(index, editBookDescriptor);
    }
}
