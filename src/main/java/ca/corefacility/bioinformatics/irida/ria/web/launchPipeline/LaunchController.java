package ca.corefacility.bioinformatics.irida.ria.web.launchPipeline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.ria.web.services.UICartService;

/**
 * Controller for workflow launch pages.
 */
@Controller
@RequestMapping("/launch")
public class LaunchController {
    private UICartService cartService;

    @Autowired
    public void setCartService(UICartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Mapping for the pipeline launch page.
     * @return The path to the launch page html file.
     */
    @GetMapping("")
    public String getPipelineLaunchPage() {
        if(cartService.isCartEmpty()) {
            // User cannot launch a pipeline if the cart is empty.
            return "redirect:/cart/pipelines";
        }
        return "launch";
    }
}