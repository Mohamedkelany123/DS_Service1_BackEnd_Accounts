package com.example.service1.controller;

import com.example.service1.entities.customeraccount;
import com.example.service1.entities.orders;
import com.example.service1.entities.product;
import com.example.service1.entities.sellingcompanysoldproducts;
import com.example.service1.services.CustomerAccountService;
import com.example.service1.services.GeographicCoverageService;
import com.example.service1.services.ProductSellingCompanyAccountService;
import com.example.service1.services.PurchaseOrderService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateful;
import jakarta.persistence.*;
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

    @EJB
    ProductSellingCompanyAccountService productSellingCompanyAccountService;

//    @EJB
//    private GeographicCoverageService geographicCoverageService;

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
            customerAccountService.register(customer.getName(), customer.getUsername(), customer.getPassword(), customer.getLocation(), customer.getEmail());
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
                int totalPrice = 0;
                boolean modified = false;
                for (String item : cart) {
                    List<product> products = entityManager.createQuery("SELECT p FROM product p WHERE p.name = :item AND p.quantity > 0")
                            .setParameter("item", item)
                            .getResultList();
                    if (products.size() > 0) {
                        success += 1;
                        modified = true;
                    }
                }
                if (success != cart.size() || !modified) {
                    cart.clear();
                    throw new Exception("None of the updates were successful.");
                }
                for (String item : cart) {
                    Query query = entityManager.createQuery("SELECT p.price FROM product p WHERE p.name = :item");
                    query.setParameter("item", item);
                    Integer price = (Integer) query.getSingleResult();
                    totalPrice += price;
                }

                Long orderId = 0L;
                if (cart.size() > 0) {
                    String cartStr = String.join(",", cart);
                    TypedQuery<String> queryLocation = entityManager.createQuery("SELECT ca.location FROM customeraccount ca WHERE ca.username = :username", String.class);
                    queryLocation.setParameter("username", username);
                    String location = queryLocation.getSingleResult();
                    orders order = new orders(username, cartStr, "-", "pending", totalPrice, location);

                    EntityManager entityManager = null;

                    try {
                        entityManager = emf.createEntityManager();
                        entityManager.getTransaction().begin();
                        entityManager.persist(order);
                        entityManager.getTransaction().commit();
                        for (String item : cart) {
                            int rowsUpdated = entityManager.createQuery("UPDATE product p SET p.quantity = p.quantity - 1 WHERE p.name = :item AND p.quantity > 0")
                                    .setParameter("item", item)
                                    .executeUpdate();
                        }
                        for (String item : cart) {
                            Query query = entityManager.createQuery("SELECT p.sellerName FROM product p WHERE p.name = :item");
                            query.setParameter("item", item);
                            String selling_company_name = (String) query.getSingleResult();


                            System.out.println("BEFORE QUERY 2");

                            try {
                                TypedQuery<Long> query2 = entityManager.createQuery(
                                        "SELECT MAX(p.id) FROM orders p WHERE p.customerName = :name", Long.class);
                                query2.setParameter("name", order.getCustomerName());
                                orderId = query2.getSingleResult();
                            } catch (NoResultException e) {
                                System.out.println("1:" + e);
                            } catch (NonUniqueResultException e) {
                                System.out.println("2 CATCH:" + e);
                            } catch (Exception e) {
                                System.out.println("3:" + e);
                            }

                            if(!GeographicCoverageService.getInstance().isRegionSupported(location)){
                                customerAccountService.sendToQueue("Order Location Not Covered!", username, orderId);
                                Query queryDelete = entityManager.createQuery("DELETE FROM orders o WHERE o.id = :orderId");
                                queryDelete.setParameter("orderId", orderId);
                                queryDelete.executeUpdate();
                                throw new RuntimeException("Order Location Not Covered!");
                            }

                            System.out.println("ORDER ID:" + orderId);
                            sellingcompanysoldproducts sold = new sellingcompanysoldproducts(username, selling_company_name, "-", item, "pending", orderId);
                            System.out.println("usernmae: " + sold.getCustomer_name());
                            System.out.println("sellingCompany: " + sold.getSelling_company_name());
                            System.out.println("shippingCompany: " + sold.getShipping_company());
                            System.out.println("Product: " + sold.getProduct());
                            System.out.println("Status: " + sold.getStatus());
                            System.out.println("BEFORE ADD SOLD PRODUCT");
                            productSellingCompanyAccountService.addSoldProduct(sold);
                            customerAccountService.sendToQueue("Order ID[" +orderId+ "]Successfully Added!", username, orderId);
                            ///////////////////////////////////////////

                        }
                        //////////////////////////////////////////
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
