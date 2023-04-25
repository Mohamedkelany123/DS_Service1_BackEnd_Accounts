package com.example.service1.controller;

import com.example.service1.entities.customeraccount;
import com.example.service1.entities.orders;
import com.example.service1.entities.product;
import com.example.service1.services.CustomerAccountService;
import com.example.service1.services.PurchaseOrderService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/customer")
@Stateful
public class CustomerController {

    @EJB
    private CustomerAccountService customerAccountService;

    @EJB
    private PurchaseOrderService purchaseOrderService;

    private ArrayList<String> cart = new ArrayList<>();

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerCustomer(customeraccount customer, @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        try {
            System.out.println("CREATING SHIPPING COMPANY");
            customerAccountService.register(customer.getName(), customer.getUsername(), customer.getPassword(), customer.getLocation());
            //session.setAttribute("username", customer.getUsername());
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to register Customer").build();
        }
    }

    @POST
    @Path("/login/{username}/{password}")
    public Response loginCustomer(@PathParam("username") String username, @PathParam("password") String password, @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (customerAccountService.login(username, password)) {
            session.setAttribute("username", username);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    @POST
    @Path("/addProductToCart/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToCart(@PathParam("name") String name, @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No user loggedIn").build();
        } else {
            TypedQuery<product> query = entityManager.createQuery("SELECT p FROM product p WHERE p.name = :name AND p.quantity > 0", product.class);
            query.setParameter("name", name);

            List<product> resultList = query.getResultList();

            if (!resultList.isEmpty()) {
                // The query returned at least one result
                try {
                    List<String> cart = (List<String>) session.getAttribute("cart");
                    if (cart == null) {
                        cart = new ArrayList<>();
                        session.setAttribute("cart", cart);
                    }
                    cart.add(name);
                    return Response.status(Response.Status.CREATED).build();
                } catch (Exception e) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to add product").build();
                }
            } else {
                // The query did not return any results
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Product sold out").build();
            }
        }

    }

    @GET
    @Path("/listCartItems")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listCartItems(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No user loggedIn").build();
        } else {

            try {
                List<String> cart = (List<String>) session.getAttribute("cart");
                if (cart == null) {
                    cart = new ArrayList<>();
                }
                return Response.ok(cart).build();
            } catch (Exception e) {
                return Response.serverError().build();
            }
        }
    }


    @POST
    @Path("/removeProduct/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeProduct(@PathParam("name") String name, @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No user loggedIn").build();
        } else {

            try {
                List<String> cart = (List<String>) session.getAttribute("cart");
                if (cart == null) {
                    cart = new ArrayList<>();
                    session.setAttribute("cart", cart);
                }
                cart.remove(name);
                return Response.status(Response.Status.CREATED).build();
            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to remove product").build();
            }
        }
    }

    @GET
    @Path("/clearCart")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearCart(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No user loggedIn").build();
        } else {
            try {
                List<String> cart = (List<String>) session.getAttribute("cart");
                if (cart == null) {
                    cart = new ArrayList<>();
                    session.setAttribute("cart", cart);
                }
                cart.clear();
                return Response.status(Response.Status.CREATED).build();
            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to clear cart").build();
            }
        }
    }

    @GET
    @Path("/listProducts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<product> listProducts(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return null;
        } else {
            TypedQuery<product> query = entityManager.createQuery("SELECT p FROM product p", product.class);
            return query.getResultList();
        }
    }

    @POST
    @Path("/purchase")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response purchase(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No user loggedIn").build();
        } else {
            try {
                List<String> cart = (List<String>) session.getAttribute("cart");
                if (cart == null) {
                    cart = new ArrayList<>();
                    session.setAttribute("cart", cart);
                }
                int success = 0;
                boolean modified = false;
                for (String item : cart) {
                    int rowsUpdated = entityManager.createQuery("UPDATE product p SET p.quantity = p.quantity - 1 WHERE p.name = :item AND p.quantity > 0")
                            .setParameter("item", item)
                            .executeUpdate();
                    if (rowsUpdated > 0) {
                        success += 1;
                        modified = true;
                    }
                }
                if (success != cart.size() || !modified) {
                    cart.clear();
                    throw new Exception("None of the updates were successful.");
                }

                if (cart.size() > 0) {
                    String cartStr = String.join(",", cart);
                    orders order = new orders(username, cartStr, "-", "pending");

                    EntityManager entityManager = null;

                    try {
                        entityManager = emf.createEntityManager();
                        entityManager.getTransaction().begin();
                        entityManager.persist(order);
                        entityManager.getTransaction().commit();

                        //IF THE PURCHASE IS COMMITED THE CART SHOULD BE CLEARED
                        cart.clear();
                    } catch (Exception e) {
                        if (entityManager != null && entityManager.getTransaction().isActive()) {
                            entityManager.getTransaction().rollback();
                        }
                        throw e;
                    } finally {
                        if (entityManager != null) {
                            entityManager.close();
                        }
                    }
                }
                return Response.status(Response.Status.CREATED).build();
            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to purchase order").build();
            }
        }
    }


    @GET
    @Path("/viewCurrentOrders")
    @Produces(MediaType.APPLICATION_JSON)
    public List<orders> viewOrders(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return null;
        } else {
            TypedQuery<orders> query = entityManager.createQuery("SELECT o FROM orders o WHERE o.customerName = :name AND o.status IN ('pending', 'processing')", orders.class);
            query.setParameter("name", username);
            List<orders> orders = query.getResultList();
            return orders;
        }
    }

    @GET
    @Path("/viewPastOrders")
    @Produces(MediaType.APPLICATION_JSON)
    public List<orders> viewPastOrders(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return null;
        } else {
            TypedQuery<orders> query = entityManager.createQuery("SELECT o FROM orders o WHERE o.customerName = :name AND o.status = 'shipped'", orders.class);
            query.setParameter("name", username);
            List<orders> orders = query.getResultList();
            return orders;
        }
    }

    @POST
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Response.ok().build();
    }
}
