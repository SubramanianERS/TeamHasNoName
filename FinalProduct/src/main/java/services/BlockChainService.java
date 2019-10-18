package services;

import beans.Block;
import beans.Transaction;
import delegate.BlockChainDelegate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class BlockChainService {

    @RequestMapping(method = RequestMethod.POST)
    public HashMap<String, List<Block>> postIndex(@RequestBody List<List<Transaction>> transactions) throws ParseException {

        return BlockChainDelegate.createBlockChain(transactions);
    }

    @RequestMapping(value = "/**",method = RequestMethod.OPTIONS)
    public String getOption(HttpServletResponse response, Model model)
    {
        response.setHeader("Access-Control-Allow-Origin","*");

        response.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");

        return "";
    }

}
