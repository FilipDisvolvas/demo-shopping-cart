package net.sakrak.demoshoppingcart.controllers

import net.sakrak.demoshoppingcart.commands.LoginCommand
import net.sakrak.demoshoppingcart.services.BasketService
import net.sakrak.demoshoppingcart.services.CustomerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.lang.Nullable
import org.springframework.stereotype.Component
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class CustomerLoginInterceptorImpl : CustomerLoginInterceptor {
    @Autowired
    lateinit var customerService: CustomerService

    @Autowired
    lateinit var basketService: BasketService

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        @Nullable modelAndView: ModelAndView?
    ) {
        if (modelAndView == null || modelAndView.view is RedirectView) {
            return
        }

        val customerId : Long?  = request.session.getAttribute("customerId") as Long?

        if (customerId != null) {
            val customer = customerService.findById(customerId)
            val basketEntries = basketService.getBasketEntries(customerId)
            modelAndView.model["greetingCustomerName"] = customer!!.firstName
            modelAndView.model["greetingBasketEntries"] = basketEntries.sumOf { it.quantity }
        } else {
            modelAndView.model["loginCommand"] = LoginCommand(email = null, password = null)
        }
    }
}